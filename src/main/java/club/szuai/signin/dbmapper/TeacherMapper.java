package club.szuai.signin.dbmapper;

import club.szuai.signin.bean.Teacher;

import java.util.List;
import java.util.Map;

public interface TeacherMapper {
    int deleteByPrimaryKey(Integer teacherId);

    int insert(Teacher record);

    int insertSelective(Teacher record);

    Teacher selectByPrimaryKey(Integer teacherId);

    int updateByPrimaryKeySelective(Teacher record);

    int updateByPrimaryKey(Teacher record);

    List<Teacher> getList(Map<String, Object> params);

    Teacher selectByIdAndPassword(Map<String, Object> params);
}