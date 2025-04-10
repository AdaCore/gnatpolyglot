" Vim syntax file
" Language:     Jte
if version < 600
  syntax clear
elseif exists("b:current_syntax")
  finish
endif

if !exists("main_syntax")
  let main_syntax = "ada"
endif

"Source the cpp syntax file
ru! syntax/cpp.vim
unlet b:current_syntax

"Put the java syntax file in @javaTop
syn include @javaTop syntax/java.vim

" End keywords
syn keyword jteEnd contained endfor endif else if for elseif

" Block rules
syn region jteCond matchgroup=jteDelim start=#^\s*@if# end=#$# keepend contains=@javaParenE,jteComment
syn region jteLine matchgroup=jteDelim start=#^\s*@# end=#$# keepend contains=@javaTop,jteEnd,jteComment
syn region jteBlock matchgroup=jteDelim start=#!{\s\?# end=#}# keepend contains=@javaTop,jteEnd

syn region jteCall matchgroup=jteDelim start=#@template.[a-zA-Z][a-zA-Z0-9]*(# end=#)# keepend contains=@javaTop,jteEnd

" Variables
syn region jteNested start="{" end="}" transparent display contained contains=jteNested,@javaTop
syn region jteVariable matchgroup=jteDelim start=#\${# end=#}# contains=jteNested,@javaTop

" Comments
syn region jteComment start="<%--" end="--%>"

" Newline Escapes
syn match jteEscape /\\$/

" Default highlighting links
if version >= 508 || !exists("did_jte_syn_inits")
  if version < 508
    let did_jte_syn_inits = 1
    com -nargs=+ HiLink hi link <args>
  else
    com -nargs=+ HiLink hi def link <args>
  endif

  HiLink jteDocComment jteComment
  HiLink jteDefEnd jteDelim

  HiLink jteAttributeKey Type
  HiLink jteAttributeValue String
  HiLink jteText Normal
  HiLink jteDelim Preproc
  HiLink jteEnd Keyword
  HiLink jteComment Comment
  HiLink jteEscape Special

  delc HiLink
endif

let b:current_syntax = "eruby"

set shiftwidth=3
set softtabstop=3
