package ru.innotech.lesson03;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;

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
    private Map<Map<Field, Object>, Map<Method, DataCache>> cacheValues = new ConcurrentHashMap<>(); // кэшированные значения

    private Map<Field, Object> currentState = new ConcurrentHashMap<>();  // текущее состояние объекта

    public static List<CacheClass> cacheObjects = new CopyOnWriteArrayList<>(); // список всех закэшированных объектов

    public CacheClass(Object object) {
        this.object = object;
        cacheObjects.add(this); // добавляем закэшированный объект в общий список
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
        currentState = new ConcurrentHashMap<>();
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
        synchronized (this) {   // чтобы не было конфликта с выполнением кэширования и с другими потоками очистки
            for (Map<Field, Object> keyF : cacheValues.keySet()) {
                for (Method keyM : cacheValues.get(keyF).keySet()) {
                    if (cacheValues.get(keyF).get(keyM).checkLifeTime()) {
                        cacheValues.get(keyF).remove(keyM); // удаляем кэш для данного метода
                    }
                }
                if (cacheValues.get(keyF).isEmpty()) { // удалим из кэша запись и для состояния
                    cacheValues.remove(keyF);
                }
            }
        }
    }

    // очистка кэша для всех объектов
    public static void cleanCacheAll () {
        for (CacheClass o : cacheObjects) {
            o.cleanCache();
        }
    }

    // запуск очистки кэша через демон-поток
    public static void startClean() {
        if (CacheClass.countCacheValueAll() < 1) return;    // запуск очистки будет производиться только если количество закэшированных значений больше заданной велечины
        Thread thread = new Thread(()-> CacheClass.cleanCacheAll());
        thread.setDaemon(true);
        thread.start();
    }

    // возвращает количество закэшированных значений для объекта
    public int countCacheValue() {
        int countValue = 0;
        for (Map<Field, Object> keyF : cacheValues.keySet())
            countValue += cacheValues.get(keyF).size();
        return countValue;
    }

    // возвращает количество закэшированных значений для всех объектов
    public static int countCacheValueAll () {
        int countValueAll = 0;
        for (CacheClass o : cacheObjects) {
            countValueAll += o.countCacheValue();
        }
        return countValueAll;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object retValueMethod;

        Method methodObj = getMethodInClass(method); // получим метод объекта

        if (methodObj.isAnnotationPresent(Mutator.class)) { // только мутатор меняет состояние
            retValueMethod = method.invoke(object, args);
            setСurrentState(); // пересчитаем текущее состояние объекта
            startClean();  // если нужно, запускаем thread для очистки кэша
        }
        else if (methodObj.isAnnotationPresent(Cache.class)) {
            long lifeTime = methodObj.getAnnotation(Cache.class).value();   // время жизни кэша

            if (currentState.isEmpty()) setСurrentState(); // если текущее состояние объекта ещё не было рассчитано, рассчитаем его

            synchronized (this) { // чтобы не было конфликта с очисткой кэша
                if (!cacheValues.containsKey(currentState)) {  // если кэширования для данного состояния пока нет
                    Map<Method, DataCache> methodCache = new ConcurrentHashMap<>();
                    methodCache.put(methodObj, new DataCache(method.invoke(object, args), lifeTime));
                    cacheValues.put(currentState, methodCache);
                } else if (!cacheValues.get(currentState).containsKey(methodObj))  // если этот метод ещё не кэшировался для данного состояния
                    cacheValues.get(currentState).put(methodObj, new DataCache(method.invoke(object, args), lifeTime));
                else
                    cacheValues.get(currentState).get(methodObj).recalcLifeTime(lifeTime);  // значит значение уже закэшировано, обновим срок его жизни

                retValueMethod = cacheValues.get(currentState).get(methodObj).getValue();   // возвращаем значение из кэша
            }
        }
        else retValueMethod = method.invoke(object, args);  // никаких аннотаций нет, просто выполним метод

        return retValueMethod;
    }
}

// кэшированные результаты выполнения методов
class DataCache {
    Object value;
    long dateSave;  // время создания кэша в миллисекундах с эпохи 1970-01-01T00:00:00Z
    long lifeTime;  // вермя жизни в милисекундах

    public DataCache(Object value, long lifeTime) {
        this.value = value;
        this.dateSave = System.currentTimeMillis();
        this.lifeTime = lifeTime;
    }

    public Object getValue() {
        return value;
    }

    public void recalcLifeTime(long lifeTime) {
        this.dateSave = System.currentTimeMillis();
        this.lifeTime = lifeTime;
    }

    public long getDateSave() {
        return dateSave;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    // если время жизни кэша истекло, вернёт true
    public boolean checkLifeTime() {
        if (this.lifeTime == 0) return false; // если 0, то бессрочно
        return System.currentTimeMillis() > this.dateSave + this.lifeTime;
    }
}