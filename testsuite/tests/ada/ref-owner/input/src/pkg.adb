with Ada.Unchecked_Deallocation;

package body Pkg is

   procedure Replace (A : in out String_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (String, String_Access);
   begin
      Free (A);
      A := new String'("Hello");
   end Replace;

   procedure Replace (A : in out Int_Array_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (Int_Array, Int_Array_Access);
   begin
      Free (A);
      A := new Int_Array'(1 => 42);
   end Replace;

   procedure Replace (A : in out Byte_Array_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (Byte_Array, Byte_Array_Access);
   begin
      Free (A);
      A := new Byte_Array'(1 => 1);
   end Replace;

   procedure Replace (A : in out Short_Array_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (Short_Array, Short_Array_Access);
   begin
      Free (A);
      A := new Short_Array'(1 => 2);
   end Replace;

   procedure Replace (A : in out Long_Array_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (Long_Array, Long_Array_Access);
   begin
      Free (A);
      A := new Long_Array'(1 => 3);
   end Replace;

   procedure Replace (A : in out Float_Array_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (Float_Array, Float_Array_Access);
   begin
      Free (A);
      A := new Float_Array'(1 => 4.5);
   end Replace;

   procedure Replace (A : in out Double_Array_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (Double_Array, Double_Array_Access);
   begin
      Free (A);
      A := new Double_Array'(1 => 5.5);
   end Replace;

   procedure Replace (A : in out Bool_Array_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (Bool_Array, Bool_Array_Access);
   begin
      Free (A);
      A := new Bool_Array'(1 => True);
   end Replace;

   procedure Replace (A : in out Rec_Array_Access) is
      procedure Free is new Ada.Unchecked_Deallocation (Rec_Array, Rec_Array_Access);
   begin
      Free (A);
      A := new Rec_Array'(1 => (V => 43));
   end Replace;

end Pkg;
