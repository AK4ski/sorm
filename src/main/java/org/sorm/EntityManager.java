package org.sorm;

public interface EntityManager<T> {

    static <T> EntityManager<T> of(EntityManagerConfig entityManagerConfig, Class<T> clss) {
        return new EntityManagerImpl<>(entityManagerConfig);
    }

    void persist(T entity);

    T read(long primaryKey, Class<T> clzz);
}
