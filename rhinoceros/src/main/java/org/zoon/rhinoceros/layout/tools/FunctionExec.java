package org.zoon.rhinoceros.layout.tools;

import org.hjson.JsonValue;
import org.zoon.rhinoceros.js.channel.JsChannel;
import org.zoon.rhinoceros.utils.Reflect;

public class FunctionExec {
    private String language;
    private String name;
    private Class<?>[] argumentsType;
    private Object[] argumentsValue;

    public FunctionExec(String language, String name, Object... argumentsValue) {
        this.language = language;
        this.name = name;
        argumentsType = new Class<?>[argumentsValue.length];
        this.argumentsValue = argumentsValue;
        for(int i = 0; i < argumentsValue.length; i++) {
            argumentsValue[i] = argumentsValue[i];
            argumentsType[i] = argumentsValue[i].getClass();
        }
    }

    public FunctionExec(JsonValue value, Object... argumentsValue) {
        if(value.isString()) {
            String[] arr = value.asString().split(":");
            this.language = arr[0].trim();
            this.name = arr[1].trim();
            argumentsType = new Class<?>[argumentsValue.length];
            this.argumentsValue = argumentsValue;
            for(int i = 0; i < argumentsValue.length; i++) {
                argumentsType[i] = argumentsValue[i].getClass();
            }
        }
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getArgumentsType() {
        return argumentsType;
    }

    public Object[] getArgumentsValue() {
        return argumentsValue;
    }

    public Object exec(Object javaChannel, JsChannel jsChannel, Reflect reflect) {
        if("javascript".equals(language)) {
            return jsChannel.exFunction(name, getArgumentsValue());
        }
        else {
            return reflect.clear().on(javaChannel).method(name, getArgumentsType()).invoke(getArgumentsValue());
        }
    }
}
