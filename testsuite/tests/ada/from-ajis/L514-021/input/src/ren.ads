with Base;
with Base.Sub;

package Ren is

   A : Integer renames Base.A;

   B : Base.Enum renames Base.B;

   C : Base.Enum renames Base.C;

   D : Base.Enum renames Base.D;

   E : Base.Enum renames Base.E;

   F : Base.Enum renames Ren.E;

   G : Base.Enum renames Ren.F;

   H : Integer renames Base.Sub.R;

   I : Base.Enum renames Base.Sub.S;

   J : Base.Enum renames Base.Sub.T;

end Ren;
