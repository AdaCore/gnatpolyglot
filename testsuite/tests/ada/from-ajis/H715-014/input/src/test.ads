package Test is

   type T is tagged null record;

   procedure Proc
     (V        : T;
      Callback : access function (S : String) return Boolean := null);

   procedure Call_Proc
     (V              : T'Class;
      Other_Callback : access function (S : String) return Boolean := null);

end Test;
