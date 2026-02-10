package Test.Child is

   function F (I : Not_Int) return Not_Int is (I);

   type Rec is record
      I : Not_Int;
   end record;

   G : Not_Int;

end Test.Child;

