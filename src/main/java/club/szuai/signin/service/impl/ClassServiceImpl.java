package club.szuai.signin.service.impl;

import club.szuai.signin.bean.Class;
import club.szuai.signin.dbmapper.ClassMapper;
import club.szuai.signin.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "classService")
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;


    @Override
    public int addClass(Class classes) {
        return classMapper.insert(classes);
    }

    @Override
    public List<Class> getList(int limit) {
        return classMapper.getList(limit);
    }
}
