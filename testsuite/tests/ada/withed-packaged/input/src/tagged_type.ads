with Rec5;

package Tagged_Type is

   type T is tagged null record;

   function F (V: T) return Rec5.R5 is (I => 5);

end Tagged_Type;
