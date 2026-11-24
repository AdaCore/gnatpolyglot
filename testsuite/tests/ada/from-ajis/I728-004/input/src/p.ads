package P is

   type Name_Id is new Integer;
   type Array_Id is new Integer;
   type Array_Element_Id is new Integer;
   type Variable_Id is new Integer;
   type Package_Id is new Integer;

   type Variable_Value is new String;

   type Project_Tree_Ref is null record;

   function Value_Of
     (Name      : Name_Id;
      In_Arrays : Array_Id;
      In_Tree   : Project_Tree_Ref) return Array_Element_Id;

   function Value_Of
     (Name        : Name_Id;
      In_Packages : Package_Id;
      In_Tree     : Project_Tree_Ref) return Package_Id;

   function Value_Of
     (Variable_Name : Name_Id;
      In_Variables  : Variable_Id;
      In_Tree       : Project_Tree_Ref) return Variable_Value;

end P;
