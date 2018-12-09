package club.szuai.signin.service;

import club.szuai.signin.bean.Class;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CommonService {

    int addClass(Class classes);

    List<Class> getList(int limit);

    List<Class> getClasses(int studentId);

    List<Class> getTeachingClasses(int teacherId);

    Map<String, Object> getNameList(int class_id);

    Map<String, Object>getClassroomLocation(int class_id);

    List<String> getSignInList(int class_id);

    void updateSignInList(int class_id,int student_id);


}
