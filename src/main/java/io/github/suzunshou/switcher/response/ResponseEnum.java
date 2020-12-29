package io.github.suzunshou.switcher.response;

public enum ResponseEnum {
    SUCCESS(200), ERROR(500), NULL(0);
    int code;

    public int getCode() {
        return code;
    }

    ResponseEnum(int code) {
        this.code = code;
    }
}
