package com.mycompany.abstractscrapers;

/**
 * Field Object is represented by Selector Types for field name and value,
 * and field name and value types (text, link or image)
 * @author vasgat
 */
public class Field {
   public String SelectorNameType = SelectorType.CSS;
   public String SelectorValueType = SelectorType.CSS;
   public String FieldNameType = FieldType.text;
   public String FieldValueType = FieldType.text;
   public String FieldName;
   public String FieldValue;
   
   public Field(String FieldName, String FieldValue){
      this.FieldName = FieldName;
      this.FieldValue = FieldValue;
   }
   
   public Field(String FieldName, String FieldValue, String SelectorNameType, String SelectorValueType, String FieldNameType, String FieldValueType){
      this.FieldName = FieldName;
      this.FieldValue = FieldValue;
      this.SelectorNameType = SelectorNameType;
      this.SelectorValueType = SelectorValueType;
      this.FieldNameType = FieldNameType;
      this.FieldValueType = FieldValueType;
   }   
}
