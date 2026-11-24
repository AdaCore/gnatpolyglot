with Ada.Text_IO; use Ada.Text_IO;

package body P is

   function Value_Of
     (Name      : Name_Id;
      In_Arrays : Array_Id;
      In_Tree   : Project_Tree_Ref) return Array_Element_Id is
   begin
      return 0;
   end Value_Of;

   function Value_Of
     (Name        : Name_Id;
      In_Packages : Package_Id;
      In_Tree     : Project_Tree_Ref) return Package_Id is
   begin
      return 0;
   end Value_Of;

   function Value_Of
     (Variable_Name : Name_Id;
      In_Variables  : Variable_Id;
      In_Tree       : Project_Tree_Ref) return Variable_Value is
   begin
      return "";
   end Value_Of;

end P;
