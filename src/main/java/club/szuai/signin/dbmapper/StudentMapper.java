package club.szuai.signin.dbmapper;

import club.szuai.signin.bean.Student;

import java.util.List;
import java.util.Map;

public interface StudentMapper {
    int deleteByPrimaryKey(Integer studentId);

    int insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Integer studentId);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

    List<Student> getList(Map<String, Object> params);
}