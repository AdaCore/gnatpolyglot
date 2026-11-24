with P_A;
with P_Other_Type;

package P_B is

   subtype A_Subtype is P_A.A_Type;

   subtype An_Other_Subtype is P_Other_Type.An_Integer;

end P_B;