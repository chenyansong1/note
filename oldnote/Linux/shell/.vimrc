if v:lang =~ "utf8$" || v:lang =~ "UTF-8$"
   set fileencodings=ucs-bom,utf-8,latin1
endif

set nocompatible        " Use Vim defaults (much better!)
set bs=indent,eol,start         " allow backspacing over everything in insert mode
"set ai                 " always set autoindenting on
"set backup             " keep a backup file
set viminfo='20,\"50    " read/write a .viminfo file, don't store more
                        " than 50 lines of registers
set history=50          " keep 50 lines of command line history
set ruler               " show the cursor position all the time

" Only do this part when compiled with support for autocommands
if has("autocmd")
  augroup redhat
  autocmd!
  " In text files, always limit the width of text to 78 characters
  " autocmd BufRead *.txt set tw=78
  " When editing a file, always jump to the last cursor position
  autocmd BufReadPost *
  \ if line("'\"") > 0 && line ("'\"") <= line("$") |
  \   exe "normal! g'\"" |
  \ endif
  " don't write swapfile on most commonly used directories for NFS mounts or USB sticks
  autocmd BufNewFile,BufReadPre /media/*,/run/media/*,/mnt/* set directory=~/tmp,/var/tmp,/tmp
  " start with spec file template
  autocmd BufNewFile *.spec 0r /usr/share/vim/vimfiles/template.spec
  augroup END
endif

if has("cscope") && filereadable("/usr/bin/cscope")
   set csprg=/usr/bin/cscope
   set csto=0
   set cst
   set nocsverb
   " add any database in current directory
   if filereadable("cscope.out")
      cs add $PWD/cscope.out
   " else add database pointed to by environment
   elseif $CSCOPE_DB != ""
      cs add $CSCOPE_DB
   endif
   set csverb
endif

" Switch syntax highlighting on, when the terminal has colors
" Also switch on highlighting the last used search pattern.
if &t_Co > 2 || has("gui_running")
  syntax on
  set hlsearch
endif

filetype plugin on

if &term=="xterm"
     set t_Co=8
     set t_Sb=dm
     set t_Sf=dm
endif

" Don't wake up system with blinking cursor:
" http://www.linuxpowertop.org/known.php
let &guicursor = &guicursor . ",a:blinkon0"



" 当新建 .h .c .hpp .cpp .mk .sh等文件时自动调用SetTitle 函数  
autocmd BufNewFile *.[ch],*.hpp,*.cpp,Makefile,*.mk,*.sh exec ":call SetTitle()"   
  
" 加入注释   
func SetComment()  
    call setline(1,"/*================================================================")   
    call append(line("."),   "*   Copyright (C) ".strftime("%Y")." Sangfor Ltd. All rights reserved.")  
    call append(line(".")+1, "*   ")   
    call append(line(".")+2, "*   文件名称：".expand("%:t"))   
    call append(line(".")+3, "*   创 建 者：LuZhenrong")  
    call append(line(".")+4, "*   创建日期：".strftime("%Y年%m月%d日"))   
    call append(line(".")+5, "*   描    述：")   
    call append(line(".")+6, "*")  
    call append(line(".")+7, "================================================================*/")   
    call append(line(".")+8, "")  
    call append(line(".")+9, "")  
endfunc  
  
" 加入shell,Makefile注释  
func SetComment_sh()  
    call setline(3, "#================================================================")   
    call setline(4, "#   Copyright (C) ".strftime("%Y")." Sangfor Ltd. All rights reserved.")  
    call setline(5, "#   ")   
    call setline(6, "#   文件名称：".expand("%:t"))   
    call setline(7, "#   创 建 者：LuZhenrong")  
    call setline(8, "#   创建日期：".strftime("%Y年%m月%d日"))   
    call setline(9, "#   描    述：")   
    call setline(10, "#")  
    call setline(11, "#================================================================")  
    call setline(12, "")  
    call setline(13, "")  
endfunc   
  
" 定义函数SetTitle，自动插入文件头   
func SetTitle()  
  
    if &filetype == 'make'   
        call setline(1,"")   
        call setline(2,"")  
        call SetComment_sh()  
  
    elseif &filetype == 'sh'   
        call setline(1,"#!/system/bin/sh")   
        call setline(2,"")  
        call SetComment_sh()  
          
    else  
         call SetComment()  
         if expand("%:e") == 'hpp'   
          call append(line(".")+10, "#ifndef _".toupper(expand("%:t:r"))."_H")   
          call append(line(".")+11, "#define _".toupper(expand("%:t:r"))."_H")   
          call append(line(".")+12, "#ifdef __cplusplus")   
          call append(line(".")+13, "extern \"C\"")   
          call append(line(".")+14, "{")   
          call append(line(".")+15, "#endif")   
          call append(line(".")+16, "")   
          call append(line(".")+17, "#ifdef __cplusplus")   
          call append(line(".")+18, "}")   
          call append(line(".")+19, "#endif")   
          call append(line(".")+20, "#endif //".toupper(expand("%:t:r"))."_H")   
  
         elseif expand("%:e") == 'h'   
        call append(line(".")+10, "#pragma once")   
  
         elseif &filetype == 'c'   
        call append(line(".")+10,"#include \"".expand("%:t:r").".h\"")   
  
         elseif &filetype == 'cpp'   
        call append(line(".")+10, "#include \"".expand("%:t:r").".h\"")   
  
         endif  
  
    endif  
endfunc  