package Test is

   type Rec is record
      null;
   end record;

   type An_Array is array (Integer range <>) of Integer;

   type Rec_Access is access all Rec;
   type An_Array_Access is access all An_Array;

   function Create_Null_Rec return Rec_Access;
   function Create_Null_An_Array return An_Array_Access;

   function Is_Null_Rec
     (B : Boolean; X : Rec_Access) return Boolean;

   function Is_Null_An_Array
     (B : Boolean; X : An_Array_Access) return Boolean;


end Test;