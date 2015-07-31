package abstractscrapers.src;

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
   
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Field)) return false;
        Field field = (Field) o;
        return this.FieldName.equals(field.FieldName);
    }
    
    @Override
    public int hashCode() {
      return this.FieldName.hashCode();
    }      
}
