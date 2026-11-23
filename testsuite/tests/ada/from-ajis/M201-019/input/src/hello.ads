with Hello_Impl;  use Hello_Impl;

package Hello is

   type Child is new Parent with null record;

   procedure Say (Msg : String);

end Hello;
