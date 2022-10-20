/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itb;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Artem
 */


public class HintServiceFunctional {

//данные для коннекта к базе данны   
private final String url = "jdbc:postgresql://localhost:5432/postgres";    
private final String login = "postgres";    
private final String password = "admin";  
private final String driverClass = "org.postgresql.Driver"; 

//точность триграммного поиска
public final double accuracy = 0.3;

    //функция для установления соединения с базой данных
        public Connection connect() throws ClassNotFoundException {
        Connection conn = null;

        try {
             Class.forName(driverClass);           
            conn = DriverManager.getConnection(url, login, password);    
            conn.setAutoCommit(false);
            conn.commit();               
        } 
        
        catch (SQLException e) {         
            System.out.println(e.getMessage());
        }             
 
        return conn;
    }
        
     //форматно-логический контроль входных данных
        
        public void flk(String refType, String valuePart, String limit, String offset, String scope) throws InvalidInputException {
        
        //флк для refType
        if(refType==null || refType.isEmpty()) {
         throw new InvalidInputException("Ошибка ФЛК", "Поле refType является обязательным", 204);           
          }
        
         if(!refType.equals("names") && !refType.equals("street") && !refType.equals("buildings")&& !refType.equals("districts")) {
         throw new InvalidInputException("Ошибка ФЛК","Поле refType не подходит к регулярным выражениям [districts; street; buildings; names]", 400);
           }
         
         //флк для scope
         if(refType.equals("buildings") && (scope==null || scope.isEmpty())) {
         throw new InvalidInputException("Ошибка ФЛК","Поле scope обязательно для заполнения", 204);    
         }
                  
         //флк для limit
         if(limit==null || !limit.isEmpty()) {
         
         if(!checkString(limit)) {
         throw new InvalidInputException("Ошибка ФЛК","Поле limit не подходит к регулярным выражениям [0;20]", 400);
         }
         if(Integer.parseInt(limit)>20 || Integer.parseInt(limit)<0) {
         throw new InvalidInputException("Ошибка ФЛК","Поле limit не подходит к регулярным выражениям [0;20]", 400);
         }
         
         }
 
         //флк для offset
         if(offset==null || !offset.isEmpty()) {   
             
         if(!checkString(offset)) {
         throw new InvalidInputException("Ошибка ФЛК","Поле offset не подходит к регулярным выражениям [0,1,2..]", 400);
         }
         if(Integer.parseInt(offset)<0) {
         throw new InvalidInputException("Ошибка ФЛК","Поле offset не подходит к регулярным выражениям [0,1,2..]", 400);
         }      
         
         }

        }
        
        //функция проверки того, что введенная строка есть число
        public boolean checkString(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
        
        //функция триграммного поиска для блоков подсказок по улицам, именам и районам
        public void trigramSearch(ArrayList<String> output, Connection connect, String refType, String valuePart, String limit, String offset, String scope) {
         
            String sql="";
            String final_res="";
        if( refType.equals("names") || refType.equals("street")) {
            
         sql = "select name from "+refType +" where name % '" +valuePart+"' and similarity(name, '" +valuePart+"')>="+accuracy;

    if(refType.equalsIgnoreCase("names") && (scope != null && !scope.isEmpty())) {
        
        sql=sql + " and g=" + scope;        
    } 
    
    sql = sql + "  order by similarity(name, '" +valuePart+"') desc"; 
        
        }
        
        if( refType.equals("districts")) {
            sql= "select area.short_name,districts.name FROM districts INNER JOIN area ON area.id = districts.area_id where districts.name % '" +valuePart+"' and similarity(districts.name, '" +valuePart+"')>="+accuracy;
        if(scope==null || scope.isEmpty()) 
            sql= sql + " order by districts.name";        
        else 
            sql= sql + " and area.id= "+scope+" order by districts.name"; 
        }
    
        sql = size_ResData(limit,offset,sql);
       
      try {  
          
        Statement stmt = connect.createStatement();
        ResultSet rs = stmt.executeQuery( sql );
        while ( rs.next() ) {
            if( refType.equals("names") || refType.equals("street")) output.add(rs.getString("name"));
            if( refType.equals("districts")) {
                final_res = rs.getString("short_name") + ", район " + rs.getString("name");
                output.add(final_res); 
            }
        }
        rs.close();
        stmt.close();
        connect.commit();
        
      }
         catch (SQLException e) {
            System.out.println(e.getMessage());
        }      
        
}
        
        //функция регулирования отображения выходных данных
        
        String size_ResData(String limit, String offset, String sql) {
            
        if (!limit.isEmpty()) {
        sql = sql + " LIMIT " + limit;
        }
        if (!offset.isEmpty()) {
        sql = sql + " OFFSET " + offset;
        }        
        sql = sql + ";";
        
        return sql;
        }
        
}
