package club.szuai.signin.dbmapper;

import club.szuai.signin.bean.SignIn;

import java.util.List;
import java.util.Map;

public interface SignInMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SignIn record);

    int insertAndGetId(SignIn record);

    int insertSelective(SignIn record);

    SignIn selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SignIn record);

    int updateByPrimaryKeyWithBLOBs(SignIn record);

    int updateByPrimaryKey(SignIn record);

    List<SignIn> selectByClassIdAndTime(Map<String,Object> params);

    List<SignIn> selectByClassId(Map<String,Object> params);
}