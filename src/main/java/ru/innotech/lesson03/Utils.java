package ru.innotech.lesson03;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
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
    private Map<Map<Field, Object>, Map<Method, DataCache>> cacheValues = new HashMap<>(); // кэшированные значения

    private Map<Field, Object> currentState = new HashMap<>();  // текущее состояние объекта

    public CacheClass(Object object) {
        this.object = object;
        ListObjCache.addListCache(this);
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
    // установка текущего состояния объекта
    private void setСurrentState() {
        currentState.clear();
        try {
            for (Field f : object.getClass().getDeclaredFields()){
                f.setAccessible(true);
                currentState.put(f, f.get(object));
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    // зачистка в кэше устаревших данных
    public void cleanCache() {
        for (Map<Field, Object> keyF : cacheValues.keySet()) {
            for (Method keyM : cacheValues.get(keyF).keySet()) {
                if (cacheValues.get(keyF).get(keyM).checkLifeTime()) {
                    cacheValues.get(keyF).remove(keyM); // удаляем кэш для данного метода
                }
                if (cacheValues.get(keyF).size() == 0) { // удалим из кэша запись и для состояния
                    cacheValues.remove(keyF);
                }
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method methodObj = getMethodInClass(method); // получим метод объекта
        System.out.println("---------------------------");
        if (methodObj.isAnnotationPresent(Mutator.class)) // только мутатор меняет состояние
            setСurrentState(); // пересчитаем текущее состояние объекта

        else if (methodObj.isAnnotationPresent(Cache.class)) {
            long liteTime = methodObj.getAnnotation(Cache.class).value();   // время жизни кэша

            if (currentState.size() == 0) setСurrentState(); // если текущее состояние объекта ещё не было рассчитано, рассчитаем его

            if (!cacheValues.containsKey(currentState)) {  // если кэширования для данного состояния пока нет
                Map<Method, DataCache> methodCache = new HashMap<>();
                methodCache.put(methodObj, new DataCache(method.invoke(object, args), liteTime));
                cacheValues.put(currentState, methodCache);
            } else if (!cacheValues.get(currentState).containsKey(methodObj))  // если этот метод ещё не кэшировался
                cacheValues.get(currentState).put(methodObj, new DataCache(method.invoke(object, args), liteTime));
            else
                cacheValues.get(currentState).get(methodObj).recalcLifeTime(liteTime);

            return cacheValues.get(currentState).get(methodObj).getValue();
        }
        return method.invoke(object, args);
    }
}

// кэшированные результаты выполнения методов
class DataCache {
    Object value;
    long dateSave;
    long lifeTime;

    public DataCache(Object value, long lifeTime) {
        this.value = value;
        this.dateSave = Instant.now().toEpochMilli();
        this.lifeTime = lifeTime;
    }

    public Object getValue() {
        return value;
    }

    public void recalcLifeTime(long lifeTime) {
        this.dateSave = Instant.now().toEpochMilli();
        this.lifeTime = lifeTime;
    }

    public long getDateSave() {
        return dateSave;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    // если вреся жизни кэша истекло, вернёт true
    public boolean checkLifeTime() {
        return Instant.now().toEpochMilli() > this.dateSave + this.lifeTime;
    }
}