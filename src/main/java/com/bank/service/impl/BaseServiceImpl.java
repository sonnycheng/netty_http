package com.bank.service.impl;

import java.util.List;
import java.util.Map;

import com.bank.service.IBaseService;

public class BaseServiceImpl<T> implements IBaseService<T> {
    @Override
    public T fetchById(Object id) {
        return null;
    }

    @Override
    public boolean save(T t) {
        return false;
    }

    @Override
    public boolean deleteById(Object id) {
        return false;
    }

    @Override
    public List<T> fetchList(Map query) {
        return null;
    }

    @Override
    public boolean update(T t) {
        return false;
    }
}

