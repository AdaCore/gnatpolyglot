with Test; use Test;

package Other is

   type T_Acc is access all T;

   procedure P_1 (V : T);

   procedure P_2 (V : access T);

   procedure P_3 (V : T_Acc);

end Other;