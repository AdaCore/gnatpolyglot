package body Gen is
   
   procedure Set (This : in out R; Val : T) is
   begin
      This.Val := new T'(Val);
   end Set;
   
   function Get (This : R) return T is
   begin
      return This.Val.all;
   end Get;
      
end Gen;