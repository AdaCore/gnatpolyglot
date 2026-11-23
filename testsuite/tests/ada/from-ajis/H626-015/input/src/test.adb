package body Test is

   function Create_Null_Rec return Rec_Access is
   begin
      return null;
   end Create_Null_Rec;

   function Create_Null_An_Array return An_Array_Access is
   begin
      return null;
   end Create_Null_An_Array;

   function Is_Null_Rec (B : Boolean; X : Rec_Access) return Boolean is
   begin
      return X = null;
   end Is_Null_Rec;

   function Is_Null_An_Array
     (B : Boolean; X : An_Array_Access) return Boolean is
   begin
      return X = null;
   end Is_Null_An_Array;

end Test;