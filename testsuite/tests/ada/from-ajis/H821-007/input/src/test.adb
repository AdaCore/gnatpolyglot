with Ada.Unchecked_Deallocation;

package body Test is

   G : A_Tagged_Access;

   procedure Set_Tagged_Object (V : A_Tagged_Access) is
   begin
      G := V;
   end Set_Tagged_Object;

   function Get_Tagged_Object return A_Tagged_Type'Class is
   begin
      if G = null then
         declare
            V : A_Tagged_Type;
         begin
            return V;
         end;
      else
         return G.all;
      end if;
   end Get_Tagged_Object;

   function Get_Non_Tagged_Object return A_Non_Tagged_Type is
      V : A_Non_Tagged_Type;
   begin
      return V;
   end Get_Non_Tagged_Object;

   procedure Call_Tagged_Callback (X : A_Tagged_Callback; Y : A_Tagged_Type'Class) is
   begin
      X (Y);
   end Call_Tagged_Callback;

   procedure Call_Non_Tagged_Type_Callback (X : A_Non_Tagged_Callback; Y : A_Non_Tagged_Type) is
   begin
      X (Y);
   end Call_Non_Tagged_Type_Callback;

end Test;
