/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itb;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

/**
 *
 * @author Artem
 */
@WebService(serviceName = "HintWebService")
public class HintWebService {
        
    @WebMethod(operationName = "GetHint")
    public ArrayList<String> hintService(@WebParam(name = "refType")String refType, @WebParam(name = "valuePart")String valuePart,
    @WebParam(name = "limit")String limit, @WebParam(name = "offset")String offset, @WebParam(name = "scope")String scope) throws ClassNotFoundException, InvalidInputException {
        
     HintServiceFunctional function = new HintServiceFunctional(); 
     
     ArrayList<String> output = new ArrayList<>(); //сюда буду писать результат запроса
     String final_res="";
     
    //проверяем данные на валидность 
    function.flk(refType, valuePart, limit, offset, scope);
     
     //настраиваем коннект к базе     
    Connection con = function.connect();
    
    //логика для блока street
     
    if(refType.equalsIgnoreCase("street")) {
    try {
        
        String sql = "select name from street where name ilike '" +valuePart+"%' order by name";
        
        sql = function.size_ResData(limit,offset,sql);
        
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery( sql );
        while ( rs.next() ) {
            output.add(rs.getString("name"));
        }
        rs.close();
        stmt.close();
        con.commit();
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    //если поиск по точному соответствию не дал результатов, вызываем триграммный поиск
    if(output.isEmpty()) {function.trigramSearch(output, con, refType, valuePart, limit, offset, scope);}
    }
    
    //логика для блока buildings
    
    if(refType.equalsIgnoreCase("buildings")) {
    try {
        
        String sql = "select dom,corpus,stroenie from buildings where street_id ="+scope+" and dom ilike '"+valuePart+"%' order by dom"; 

        sql = function.size_ResData(limit,offset,sql);
        
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery( sql );
        while ( rs.next() ) {
            final_res="";
            if (rs.getString("dom")!=null&&!rs.getString("dom").isEmpty())final_res = "дом "+rs.getString("dom")+"; ";
            if (rs.getString("corpus")!=null&&!rs.getString("corpus").isEmpty()) final_res=final_res+"корп. "+rs.getString("corpus")+"; ";
            if (rs.getString("stroenie")!=null&&!rs.getString("stroenie").isEmpty()) final_res=final_res+"стр. "+rs.getString("stroenie")+";";
            output.add(final_res);
        }
        rs.close();
        stmt.close();
        con.commit();
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }    
    
        //логика для блока names
    
    if(refType.equalsIgnoreCase("names")) {
    try {
        
        String sql = "select name from names where name ilike '" +valuePart+"%'"; 
        String sort = "order by name ";
        if(scope != null && !scope.isEmpty()) {
            sql=sql + " and g= " + scope + sort;
        }
        else sql=sql+sort;
        
        sql = function.size_ResData(limit,offset,sql);
                
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery( sql );
        while ( rs.next() ) {
            output.add(rs.getString("name"));
        }
        rs.close();
        stmt.close();
        con.commit();
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    
        //если поиск по точному соответствию не дал результатов, вызываем триграммный поиск
    if(output.isEmpty()) {function.trigramSearch(output, con, refType, valuePart, limit, offset, scope);} 
    }
    
            //логика для блока districts
    
    if(refType.equalsIgnoreCase("districts")) {
   
    try {
        String sql = "select area.short_name,districts.name FROM districts INNER JOIN area ON area.id = districts.area_id where districts.name ilike '" +valuePart+"%'";
        
        if(scope==null || scope.isEmpty())  
            sql = sql + " order by districts.name"; 
        else
            sql = sql + " and area.id= "+scope+" order by districts.name"; 
        
        sql = function.size_ResData(limit,offset,sql);
        
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery( sql );
        while ( rs.next() ) {
            final_res = rs.getString("short_name") + ", район " + rs.getString("name");
            output.add(final_res);
        }
        rs.close();
        stmt.close();
        con.commit();
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    
        //если поиск по точному соответствию не дал результатов, вызываем триграммный поиск
    if(output.isEmpty()) {function.trigramSearch(output, con, refType, valuePart, limit, offset, scope);} 
    }
 
    return output;
    
}
    
}


         //   select word from tbl_words where word % 'Пужкин' order by similarity(word, 'Пужкин') desc, word limit 5


         //SELECT area.short_name,districts.name FROM districts INNER JOIN area ON area.id = districts.area_id


         // select street.name, dom, corpus, street_id, stroenie FROM buildings INER JOIN street ON street_id=street.id where street_id=197269    