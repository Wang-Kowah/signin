package club.szuai.signin.service.impl;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.Student;
import club.szuai.signin.bean.Teacher;
import club.szuai.signin.dbmapper.ClassMapper;
import club.szuai.signin.dbmapper.StudentMapper;
import club.szuai.signin.dbmapper.TeacherMapper;
import club.szuai.signin.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "classService")
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public int addClass(Class classes) {
        return classMapper.insert(classes);
    }

    @Override
    public List<Class> getList(int limit) {
        return classMapper.getList(limit);
    }

    @Override
    public List<Class> getClasses(int studentId) {
        List<Class> classes = new ArrayList<>();
        try {
            Student student = studentMapper.selectByPrimaryKey(studentId);
            for (String class_id : student.getClassIds().split(",")) {
                classes.add(classMapper.selectByPrimaryKey(Integer.parseInt(class_id)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return classes;
        }
        return classes;
    }

    @Override
    public List<Class> getTeachingClasses(int teacherId) {
        List<Class> classes = new ArrayList<>();
        try {
            Teacher teacher = teacherMapper.selectByPrimaryKey(teacherId);
            for (String class_id : teacher.getClassIds().split(",")) {
                classes.add(classMapper.selectByPrimaryKey(Integer.parseInt(class_id)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return classes;
        }
        return classes;
    }


}
