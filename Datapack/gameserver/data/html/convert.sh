#!/bin/bash
#enter input encoding here
FROM_ENCODING="UTF-16LE"
#output encoding(UTF-8)
TO_ENCODING="UTF-8"
#convert
CONVERT=" iconv  -f   $FROM_ENCODING  -t   $TO_ENCODING"
#loop to convert multiple files
shopt -s globstar
for  file  in  ./**/*.htm; do
     encoding="$(file -i "$file")"
     if [[ "${encoding}" == *"utf-16le" ]]
     then
        echo "${encoding}"
        $CONVERT   "$file"   -o  "${file%.htm}.htm"
     fi
done
exit 0