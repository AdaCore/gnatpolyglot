
package Test is

   type An_Enum is (A, B, C);

   type A_Callback is access procedure
     (V1  : out Long_Long_Integer;
      V2  : in out Long_Long_Integer;
      V3  : out Float;
      V4  : in out Float;
      V5  : out Boolean;
      V6  : in out Boolean;
      V7  : out An_Enum;
      V8  : in out An_Enum;
      V9  : out Character;
      V10 : in out Character);

   procedure Call_A_Callback (V : A_Callback);

end Test;
