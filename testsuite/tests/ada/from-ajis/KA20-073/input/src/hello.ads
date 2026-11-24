with Ada.Containers.Indefinite_Vectors;
with Ada.Containers.Vectors;

package Hello is

   assert     : Integer;
   boolean    : Integer;
   break      : Integer;
   byte       : Integer;
   catch      : Integer;
   char       : Integer;
   class      : Integer;
   const      : Integer;
   continue   : Integer;
   default    : Integer;
   double     : Integer;
   enum       : Integer;
   extends    : Integer;
   final      : Integer;

   --  New compilers warn about "finally" so we mask the warning because we
   --  want to keep testing "finally" as a Java keyword.
   pragma Warnings (Off, """finally"" is a reserved word with all extensions enabled");
   finally    : Integer;
   pragma Warnings (On, """finally"" is a reserved word with all extensions enabled");

   float      : Integer;
   implements : Integer;
   import     : Integer;
   instanceof : Integer;
   int        : Integer;
   long       : Integer;
   native     : Integer;
   public     : Integer;
   short      : Integer;
   static     : Integer;
   strictfp   : Integer;
   super      : Integer;
   switch     : Integer;
   this       : Integer;
   throw      : Integer;
   throws     : Integer;
   transient  : Integer;
   try        : Integer;
   void       : Integer;
   volatile   : Integer;   

   procedure P (this : Integer);

end Hello;
