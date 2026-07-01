with Ada.Text_IO; use Ada.Text_IO;

package body Example is

    procedure Hello is
    begin
        Put_Line ("Hello world!");
    end Hello;

    procedure Hello (Name: String) is
    begin 
        Put_Line ("Hello " & Name & "!");
    end Hello;

end Example;

