
package Test is

   type Ret_Char is access
     function (V : Character) return Character;

   type Ret_Long is access
     function (V : Long_Long_Integer) return Long_Long_Integer;

   function Call_Char
     (X : Ret_Char; V : Character) return Character;

   function Call_Long
     (X : Ret_Long; V : Long_Long_Integer) return Long_Long_Integer;

end Test;
