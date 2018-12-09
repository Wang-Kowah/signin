package club.szuai.signin.service.impl;

import club.szuai.signin.bean.Class;
import club.szuai.signin.bean.*;
import club.szuai.signin.dbmapper.*;
import club.szuai.signin.service.CommonService;
import club.szuai.signin.utils.DateUtil;
import club.szuai.signin.utils.OAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "classService")
public class CommonServiceImpl implements CommonService {
    private final static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private SignInMapper signInMapper;

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
            result.put("error", 0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", 1);
        }
        result.put("idList", idList);
        result.put("nameList", nameList);
        return result;
    }

    @Override
    public Map<String, Object> getClassroomLocation(int class_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Class course = classMapper.selectByPrimaryKey(class_id);
            String address = course.getAddress();
            Location location = locationMapper.selectByBuilding(address.split(",")[0]);
            result.put("lat", location.getLat());
            result.put("lng", location.getLng());
            result.put("error", 0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", 1);
        }
        return result;
    }

    @Override
    public List<String> getSignInList(int class_id) {
        List<String> nameList = new ArrayList<>();
        int weekStartTime = (int) (DateUtil.getWeekBeginTimestamp(System.currentTimeMillis()) / 1000);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("classId", class_id);
            params.put("weekStartTime", weekStartTime);

            List<SignIn> signInList = signInMapper.selectByClassIdAndTime(params);
            if (signInList.size() == 1) {
                for (String student_id : signInList.get(0).getSigninIds().split(",")) {
                    nameList.add(studentMapper.selectByPrimaryKey(Integer.parseInt(student_id)).getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameList;
    }

    @Override
    public void updateSignInList(int class_id, int student_id) {
        int week = OAUtil.getWeekOfSemester();
        int weekStartTime = (int) (DateUtil.getWeekBeginTimestamp(System.currentTimeMillis()) / 1000);
        try {
            SignIn signIn;
            Map<String, Object> params = new HashMap<>();
            params.put("classId", class_id);
            params.put("weekStartTime", weekStartTime);

            List<SignIn> signInList = signInMapper.selectByClassIdAndTime(params);
            if (signInList.size() == 1) {
                signIn = signInList.get(0);
                String signinIds = signIn.getSigninIds();
                //确保不重复写入
                if (!signinIds.contains(student_id + "")) {
                    if (signinIds.trim().equals("")) {
                        signIn.setSigninIds(student_id + "");
                    } else {
                        signIn.setSigninIds(signinIds + "," + student_id);
                    }
                    signInMapper.updateByPrimaryKey(signIn);
                }
            } else if (signInList.isEmpty()) {
                SignIn newSignIn = new SignIn();
                newSignIn.setClassId(class_id);
                newSignIn.setWeekStartTime(weekStartTime);
                newSignIn.setWeek(week);
                newSignIn.setSigninIds(student_id + "");
                signInMapper.insertAndGetId(newSignIn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateClassIds(int class_id, int student_id) {
        try {
            Student student = studentMapper.selectByPrimaryKey(student_id);
            String classIds = student.getClassIds();
            //确保不重复写入
            if (!classIds.contains(class_id + "")) {
                if (classIds.trim().equals("")) {
                    student.setClassIds(class_id + "");
                } else {
                    student.setClassIds(classIds + "," + class_id);
                }
                studentMapper.updateByPrimaryKey(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}