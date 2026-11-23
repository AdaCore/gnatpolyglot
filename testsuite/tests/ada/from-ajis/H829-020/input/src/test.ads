package Test is

   type A_Type is tagged null record;

   type String_Access is access all String;

   function Return_String (V : A_Type) return String_Access;

   function Callback_Return_String (V : A_Type'Class) return String_Access;

end Test;
