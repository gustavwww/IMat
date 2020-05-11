# IMat - Hur ni använder det

1. Skapa en mapp på lämpligt ställe i datorn.
2. Öppna terminalen och navigera in i mappen.
3. Skapa ett repository med ```git init```
4. Lägg till en remote ```git remote add origin (SSH länken)```
5. Ladda ner master branchen ```git pull origin master```

Ni kan skapa en local branch och byta till den med ```git checkout -b <branchname>```
och om ni vill pusha denna till github skriver ni ```git push origin <branchname>```

Vill ni istället pusha till master så är det bara att ange "master" som branchname.

Fler kommandon: https://confluence.atlassian.com/bitbucketserver/basic-git-commands-776639767.html
