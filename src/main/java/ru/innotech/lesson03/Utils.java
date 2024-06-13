package ru.innotech.lesson03;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static <T> T cache (T objectIncome) {
        return (T) Proxy.newProxyInstance(
                objectIncome.getClass().getClassLoader(),
                objectIncome.getClass().getInterfaces(),
                new CacheClass(objectIncome)
        );
    }
}

class CacheClass implements InvocationHandler {
    private Object object;
    private Map<Method, Object> cacheValues = new HashMap<>();

    public CacheClass(Object object) {
        this.object = object;
    }

    // возвращает метод из класса объекта
    private Method getMethodInClass(Method method) {
        for (Method m : object.getClass().getDeclaredMethods()) {
            // если имя метода и список типов параметров совпадает
            if (m.getName().equals(method.getName()) && Arrays.asList(m.getParameterTypes()).equals(Arrays.asList(method.getParameterTypes())))
                return m;
        }
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Method methodObj = getMethodInClass(method); // получим метод объекта

        if (methodObj.isAnnotationPresent(Mutator.class))
            cacheValues.clear();    // зачищаем кэшированные значения
        else if (methodObj.isAnnotationPresent(Cache.class)) {
            if (!cacheValues.containsKey(method))   // если кэширования для данного метода пока нет
                cacheValues.put(method, method.invoke(object, args));   // сохраним значение в кэше
            return cacheValues.get(method);
        }
        return method.invoke(object, args);
    }
}