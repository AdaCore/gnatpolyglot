package Test is

   type Object is private;

   type Object_Access is access all Object;

   function Get_Object (I : Integer) return Object;

private

   type Object is null record;

end Test;
