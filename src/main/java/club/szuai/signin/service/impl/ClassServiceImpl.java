package club.szuai.signin.service.impl;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.Location;
import club.szuai.signin.bean.Student;
import club.szuai.signin.bean.Teacher;
import club.szuai.signin.dbmapper.ClassMapper;
import club.szuai.signin.dbmapper.LocationMapper;
import club.szuai.signin.dbmapper.StudentMapper;
import club.szuai.signin.dbmapper.TeacherMapper;
import club.szuai.signin.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "classService")
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private LocationMapper locationMapper;

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

    @Override
    public Map<String, Object> getNameList(int class_id) {
        Map<String, Object> result = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        try {
            Class course = classMapper.selectByPrimaryKey(class_id);
            for (String student_id : course.getStudentIds().split(",")) {
                idList.add(student_id);
                nameList.add(studentMapper.selectByPrimaryKey(Integer.parseInt(student_id)).getName());
            }
            result.put("error",0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error",1);
        }
        result.put("idList",idList);
        result.put("nameList",nameList);
        return result;
    }

    @Override
    public Map<String, Object> getClassroomLocation(int class_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Class course = classMapper.selectByPrimaryKey(class_id);
            String address = course.getAddress();
            Location location = locationMapper.selectByBuilding(address.split(",")[0]);
            result.put("lat",location.getLat());
            result.put("lng",location.getLng());
            result.put("error",0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error",1);
        }
        return result;
    }

    @Override
    public List<String> getSignInList(int class_id) {
        List<String> signInList = new ArrayList<>();

        return null;
    }

    @Override
    public void updateSignInList(int class_id,int student_id) {
        List<String> signInList = new ArrayList<>();




    }


}
