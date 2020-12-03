package com.sky.build;

import com.google.common.base.Strings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import com.sky.dto.YapiDubboDTO;
import com.sky.util.DesUtil;
import com.sky.util.JsonUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.codehaus.jettison.json.JSONException;


public class BuildJsonForDubbo{

    private static NotificationGroup notificationGroup;

    static {
        notificationGroup = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);
    }


    /**
     * 批量生成接口数据
     *
     * @param e the e
     * @return the array list
     */
    public ArrayList<YapiDubboDTO> actionPerformedList(AnActionEvent e){
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        String selectedText=e.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel().getSelectedText();
        Project project = editor.getProject();
        if(Strings.isNullOrEmpty(selectedText)){
            Notification error = notificationGroup.createNotification("please select method or class", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
            return null;
        }
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});
        ArrayList<YapiDubboDTO> yapiDubboDTOS=new ArrayList<>();
        if(selectedText.equals(selectedClass.getName())){
            PsiMethod[] psiMethods=selectedClass.getMethods();
            for(PsiMethod psiMethodTarget:psiMethods) {
                //去除私有方法
                if(!psiMethodTarget.getModifierList().hasModifierProperty("private")) {
                    yapiDubboDTOS.add(this.actionPerformed(selectedClass, psiMethodTarget, project, psiFile));
                }
            }
        }else{
            PsiMethod[] psiMethods =selectedClass.getAllMethods();
            //寻找目标Method
            PsiMethod psiMethodTarget=null;
            for(PsiMethod psiMethod:psiMethods){
                if(psiMethod.getName().equals(selectedText)){
                    psiMethodTarget=psiMethod;
                    break;
                }
            }
            yapiDubboDTOS.add(actionPerformed(selectedClass,psiMethodTarget,project,psiFile));
        }
        return yapiDubboDTOS;

    }


    /**
     * Action performed yapi dubbo dto.
     *
     * @param selectedClass   the selected class
     * @param psiMethodTarget the psi method target
     * @param project         the project
     * @param psiFile         the psi file
     * @return the yapi dubbo dto
     */
    public YapiDubboDTO actionPerformed(PsiClass selectedClass,PsiMethod psiMethodTarget,Project project,PsiFile psiFile) {
        YapiDubboDTO yapiDubboDTO=new YapiDubboDTO();
        ArrayList list=new ArrayList<KV>();
        //判断是否有匹配的目标方法
        if(psiMethodTarget!=null){
            try {
                // 获得响应
                yapiDubboDTO.setResponse(BuildJsonForYApi.getResponse(project,psiMethodTarget.getReturnType()));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            PsiParameter[] psiParameters= psiMethodTarget.getParameterList().getParameters();
            for(PsiParameter psiParameter:psiParameters){
                if(psiParameter.getType() instanceof PsiPrimitiveType){
                   //如果是基本类型
                    KV kvClass= KV.create();
                    kvClass.set(psiParameter.getType().getCanonicalText(),NormalTypes.NORMAL_TYPES
                            .get(psiParameter.getType().getPresentableText()));
                    list.add(kvClass);
                }else if(NormalTypes.isNormalType(psiParameter.getType().getPresentableText())){
                   //如果是包装类型
                    KV kvClass= KV.create();
                    kvClass.set(psiParameter.getType().getCanonicalText(),NormalTypes.NORMAL_TYPES
                            .get(psiParameter.getType().getPresentableText()));
                    list.add(kvClass);
                }else if(psiParameter.getType().getPresentableText().startsWith("List")){
                    ArrayList listChild=new ArrayList<>();
                    String[] types=psiParameter.getType().getCanonicalText().split("<");
                    if(types.length>1){
                        String childPackage=types[1].split(">")[0];
                        if(NormalTypes.NORMAL_TYPES_PACKAGES.keySet().contains(childPackage)){
                            listChild.add(NormalTypes.NORMAL_TYPES_PACKAGES.get(childPackage));
                        }else if(NormalTypes.COLLECT_TYPES_PACKAGES.containsKey(childPackage)){
                            listChild.add(NormalTypes.COLLECT_TYPES_PACKAGES.get(childPackage));
                        }else {
                            PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childPackage, GlobalSearchScope.allScope(project));
                            KV kvObject = getFields(psiClassChild, project);
                            listChild.add(kvObject);
                        }
                    }
                    KV kvClass= KV.create();
                    kvClass.set(types[0],listChild);
                    list.add(kvClass);
                }else if(psiParameter.getType().getPresentableText().startsWith("Set")){
                    HashSet setChild=new HashSet();
                    String[] types=psiParameter.getType().getCanonicalText().split("<");
                    if(types.length>1){
                        String childPackage=types[1].split(">")[0];
                        if(NormalTypes.NORMAL_TYPES_PACKAGES.keySet().contains(childPackage)){
                            setChild.add(NormalTypes.NORMAL_TYPES_PACKAGES.get(childPackage));
                        }else if(NormalTypes.COLLECT_TYPES_PACKAGES.containsKey(childPackage)){
                            setChild.add(NormalTypes.COLLECT_TYPES_PACKAGES.get(childPackage));
                        }else {
                            PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childPackage, GlobalSearchScope.allScope(project));
                            KV kvObject = getFields(psiClassChild, project);
                            setChild.add(kvObject);
                        }

                    }
                    KV kvClass= KV.create();
                    kvClass.set(types[0],setChild);
                    list.add(kvClass);
                }else if(psiParameter.getType().getPresentableText().startsWith("Map")){
                    HashMap hashMapChild=new HashMap();
                    String[] types=psiParameter.getType().getCanonicalText().split("<");
                    if(types.length>1){
                        hashMapChild.put("String","Object");
                    }
                    KV kvClass= KV.create();
                    kvClass.set(types[0],hashMapChild);
                    list.add(kvClass);
                }else{
                    PsiClass psiClassChild=  JavaPsiFacade.getInstance(project).findClass(psiParameter.getType().getCanonicalText(), GlobalSearchScope.allScope(project));
                    KV kvObject=getFields(psiClassChild,project);
                    String classNameChild=((PsiJavaFileImpl) psiClassChild.getContext()).getPackageName()+"."+psiClassChild.getName();
                    KV kvClass= KV.create();
                    kvClass.set(classNameChild,kvObject);
                    list.add(kvClass);
                }
            }
        }else{
            Notification error = notificationGroup.createNotification("please check method name", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
            return null;
        }
        try {
            String json = JsonUtil.writeValueAsString(list, true);
            yapiDubboDTO.setParams(json);
            String packageName="/"+((PsiJavaFileImpl) psiFile).getPackageName()+"."+selectedClass.getName()+"/1.0/"+psiMethodTarget.getName();
            yapiDubboDTO.setPath(packageName);
            yapiDubboDTO.setDesc("<pre><code> "+psiMethodTarget.getText()  +" </code></pre>");
            yapiDubboDTO.setTitle(DesUtil.getDescription(psiMethodTarget));
            return yapiDubboDTO;
        } catch (Exception ex) {
            Notification error = notificationGroup.createNotification("Convert to JSON failed.", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
        }
        return null;
    }



    /**
     * 获得对象属性
     * @param psiClass
     * @param project
     * @return
     */
    public static KV getFields(PsiClass psiClass, Project project) {
        KV kv = KV.create();
        if (psiClass != null) {
            String pName=psiClass.getName();
            for (PsiField field : psiClass.getAllFields()) {
                if(field.getModifierList().hasModifierProperty("final")){
                    continue;
                }
                PsiType type = field.getType();
                String name = field.getName();
                // 如果是基本类型
                if (type instanceof PsiPrimitiveType) {
                    kv.set(name, PsiTypesUtil.getDefaultValueOfType(type));
                } else {
                    //reference Type
                    String fieldTypeName = type.getPresentableText();
                    //normal Type
                    if (NormalTypes.isNormalType(fieldTypeName)) {
                        kv.set(name, NormalTypes.NORMAL_TYPES.get(fieldTypeName));
                    } else if(!(type instanceof PsiArrayType)&&((PsiClassReferenceType) type).resolve().isEnum()) {
                        kv.set(name, fieldTypeName);
                    } else if (type instanceof PsiArrayType) {
                        //array type
                        PsiType deepType = type.getDeepComponentType();
                        ArrayList list = new ArrayList<>();
                        String deepTypeName = deepType.getPresentableText();
                        if (deepType instanceof PsiPrimitiveType) {
                            list.add(PsiTypesUtil.getDefaultValueOfType(deepType));
                        } else if (NormalTypes.isNormalType(deepTypeName)) {
                            list.add(NormalTypes.NORMAL_TYPES.get(deepTypeName));
                        } else {
                            if(!pName.equals(PsiUtil.resolveClassInType(deepType).getName())) {
                                list.add(getFields(PsiUtil.resolveClassInType(deepType), project));
                            }else{
                                list.add(pName);
                            }
                        }
                        kv.set(name, list);
                    } else if (fieldTypeName.startsWith("List")) {
                        //list type
                        PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                        PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                        ArrayList list = new ArrayList<>();
                        String classTypeName = iterableClass.getName();
                        if (NormalTypes.isNormalType(classTypeName)) {
                            list.add(NormalTypes.NORMAL_TYPES.get(classTypeName));
                        } else {
                            if(!pName.equals(iterableClass.getName())) {
                                list.add(getFields(iterableClass, project));
                            }else{
                                list.add(pName);
                            }
                        }
                        kv.set(name, list);
                    } else if(fieldTypeName.startsWith("HashMap") || fieldTypeName.startsWith("Map")){
                        //HashMap or Map
                        //   PsiType mapType = PsiUtil.extractIterableTypeParameter(type, false);
                        CompletableFuture.runAsync(()-> {
                            try {
                                TimeUnit.MILLISECONDS.sleep(700);
                                Notification warning = notificationGroup.createNotification("Map Type Can not Change,So pass", NotificationType.WARNING);
                                Notifications.Bus.notify(warning, project);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                    }else if (fieldTypeName.startsWith("Set") || fieldTypeName.startsWith("HashSet")){
                        //set hashset type
                        PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                        PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                        Set set = new HashSet();
                        String classTypeName = iterableClass.getName();
                        if (NormalTypes.isNormalType(classTypeName)) {
                            set.add(NormalTypes.NORMAL_TYPES.get(classTypeName));
                        } else {
                            if(!pName.equals(iterableClass.getName())) {
                                set.add(getFields(iterableClass, project));
                            }else{
                                set.add(pName);
                            }
                        }
                        kv.set(name, set);
                    }else {
                        //class type
                        if(!pName.equals(PsiUtil.resolveClassInType(type).getName())) {
                            kv.set(name, getFields(PsiUtil.resolveClassInType(type), project));
                        }else{
                            kv.set(name, pName);
                        }
                    }
                }
            }
        }
        return kv;
    }
}