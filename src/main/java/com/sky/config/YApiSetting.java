package com.sky.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import com.sky.util.HttpClientUtil;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * Description: YApiSetting
 * Copyright (c) Department of Research and Development/Beijing
 * All Rights Reserved.
 *
 * @author gangyf
 * @version 1.0 2019年06月14日 17:32
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class YApiSetting implements Configurable {

    /**
     * 配置 json
     */
    private JTextArea config;

    /**
     * 配置 cookies
     */
    private JTextArea cookies;

    /**
     * 持久化 配置
     */
    private final PersistentState persistentState = PersistentState.getInstance();

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "YApiSetting";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel yApi = new JPanel();
        yApi.setLayout(new GridLayoutManager(34, 3, JBUI.emptyInsets(), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("字段说明");
        yApi.add(label1, new GridConstraints(1, 0, 10, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("config");
        yApi.add(label2, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("cookies");
        yApi.add(label3, new GridConstraints(22, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        // config textarea
        config = new JTextArea();
        config.setText(StringUtils.isNotBlank(persistentState.getConfig()) ? persistentState.getConfig()
                : this.getDefaultConfig());
        yApi.add(config, new GridConstraints(11, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null,
                new Dimension(150, 50), null, 0, false));

        cookies = new JTextArea();
        cookies.setText(StringUtils.isNotBlank(persistentState.getCookies()) ? persistentState.getCookies() : "");
        yApi.add(cookies, new GridConstraints(22, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null,
                new Dimension(150, 50), null, 0, false));

        final JLabel label4 = new JLabel();
        label4.setText("isSingle : 是否单模块配置，true使用singleConfig，false则使用multipleConfig");
        yApi.add(label4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("singleConfig ： 单模块配置");
        yApi.add(label5, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("multipleConfig ： 多模块配置， key = 项目名");
        yApi.add(label6, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("");
        yApi.add(label7, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("token 获取方式 ： 打开yapi ->具体项目->设置->token 配置");
        yApi.add(label8, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("项目id 获取方式 ：点击项目，查看url 中project 后面的数字为项目id http://127.0.0.1:3000/project/72/interface/api");
        yApi.add(label9, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("yapiUrl 获取方式：部署的yapi 地址");
        yApi.add(label10, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("projectType 填写方式： 根据你要上传的接口类型决定，如果为dubbo 接口就填dubbo ，如果是api 接口就填api ");
        yApi.add(label11, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("menu 接口所在的分类");
        yApi.add(label12, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        return yApi;
    }

    @Override
    public boolean isModified() {
        if (StringUtils.isBlank(config.getText())) {
            return false;
        }
        if (StringUtils.isBlank(persistentState.getConfig())) {
            return true;
        }

        return (persistentState.getConfig().hashCode() != config.getText().hashCode()) ||
                (persistentState.getCookies().hashCode() != cookies.getText().hashCode());
    }

    @Override
    public void apply() {
        if (!persistentState.getCookies().equals(cookies.getText())) {
            // 重置httpclient
            HttpClientUtil.setHttpclientWithCookie(null);
        }

        persistentState.setCookies(cookies.getText());
        persistentState.setConfig(config.getText());
    }

    /**
     * Gets default config.
     *
     * @return the default config
     */
    private String getDefaultConfig() {
        return "{\n" +
                "  \"multipleConfig\": {\n" +
                "    \"llb-api\": {\n" +
                "      \"projectToken\": \"\",\n" +
                "      \"projectId\": \"\",\n" +
                "      \"yApiUrl\": \"\",\n" +
                "      \"menu\": \"api\",\n" +
                "      \"projectType\": \"\"\n" +
                "    },\n" +
                "    \"llb-admin-api\": {\n" +
                "      \"projectToken\": \"\",\n" +
                "      \"projectId\": \"\",\n" +
                "      \"yApiUrl\": \"\",\n" +
                "      \"menu\": \"api\",\n" +
                "      \"projectType\": \"\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

}
