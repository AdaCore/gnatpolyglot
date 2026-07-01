with Ada.Text_IO; use Ada.Text_IO;

package body Example is

    procedure Increment (C : in out Counter) is
    begin
        C.Count := C.Count + 1;
    end Increment;

    procedure Increment (C : in out Counter; N : Natural) is
    begin
        C.Count := C.Count + N;
    end Increment;

    procedure Reset (C : in out Counter) is
    begin
        C.Count := 0;
    end Reset;

end Example;
