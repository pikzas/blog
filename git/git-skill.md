常见命令

git <command> --help -h 查看帮助命令

git init 将当前文件夹作为一个git 仓库来使用

添加文件

git add  　　　　需要添加的文件名或文件夹

git add --all　　  添加所有文件

git add -A

git add .

git commit

git commit --message “the message detail” 

git commit -m "message detail"

git status

如果没有pull 代码，那么有几次commit 本地代码就是比远程代码多几个。

pikzas@Pikzas MINGW64 /c/develop/git/study (master)
$ git status
On branch master
Your branch is ahead of 'orgin/master' by 2 commits.
(use "git push" to publish your local commits)
nothing to commit, working tree clean

git add . 添加所有的文件

文件在git中的几种状态

untracked  unmodified modified deleted

文件在git中提交的状态

index（或者称为staged） 即下次commit时候要提交的文件

两种场景

1.一个新文件 git add 操作之后 文件状态由untracked → unmodified 并且进入index（也称作staged）区

2。修改一个提交过的文件 文件状态由 modified → unmodified 同时进入index区

经如index区的文件 想要撤回的操作

git reset HEAD <file / folder> 将本地仓库中上次提交的内容替换现在的工作空间内的文件（只有被add过，也就是被git管理的文件会被完全替换，新创建的文件状态不会变动，本删除的文件也能被回复回来）

git rm --cached <file> 对于进入index 但是没有进入本地仓库的文件 （就是git add 但是从来没有 git commit 的文件）是将其从index中挪出 变为untracked状态 不会删除本地文件（用-f 替换--cached 则会删除本地文件）

git rm --cached <file> 对于进入本地仓库的文件（就是git add 过了 也commit 过了的文件 本地仓库中已经有该文件了） 是将其状态变为untracked 然后从本地仓库中移除 不会删除本地文件（用-f替换--cached 则会删除本地文件）

3。创建一个Ignore 文件

在你的repo的文件夹下无论何地创建又给.gitignore 文件

# 写入注释

需要忽略的文件 可以是文件 也可以是某一类文件 也可以是某一个文件夹  

git add commit 之后 你的git便会忽略所有添加到ignore中的文件 如果有改动需要不断update 并commit 

如果文件已经commit 需要你git rm 使文件不在受到git管理

 

二、git最重要的功能 分支

git branch 查看当前本地有哪些分支

git branch myBranch 即可创建一个新的名为myBranch的分支 

使用git branch 即可看到除了master外多了一个分支 ，使用git checkout myBranch 即可切换到新创建的分支上

git checkout -b myBranch 可以达到同样的目的 

git checkout - 在上一个分支和当前分支来回切换 类似于windows 的 tab 键

git alias

git config --global alias.tree 'log --graph --decorate --pretty=oneline --abbrev-commit'

即可将一大串命令用 git tree 替代

git config <level> alias.<alias name> '<your sequence of git commands>'

git diff branch1..branch2

可以查看branch1与branch2之间的区别，哪些文件多了，少了，或者是相同的文件中哪些行有变化。

解决冲突的两种情形

一种是两人同时修改了一个文件，保留需要的部分，然后add commit

一个人删除掉了一个文件，另一个人修改了

--如果想要保留该文件，那就add 在commit

--如果想要删除，git rm 该文件 再commit

how to use stash？？？？？？？？

使用远程代码库

git clone url.git

提交本地分支到远程仓库

1.创建一个分支 git checkout -b newBranch

2.git push -u origin newBranch

-u的作用 是将当前分支和远程仓库的分支绑定，以后在pull 或者push的时候就不需要每次都制定一下了

git remote show origin

git fetch 将远程与本地仓库的区别下下来，但是并不会应用他们。git status 可以查看这些差别有哪些，用git merge 可以应用这些改动，所以git pull = git fetch + git merge

将自己本地的代码发布到远程仓库

1.在GitHub上创建一个新的仓库 （不要添加readme.md文件）

2.进入本地文件系统 git init ，然后add commit 所有需要添加的文件

3 git remote add origin https://github.com/usrename/branch.git

4.git push -u origin master

 

git的配置

git的配置分为三个级别 

　1 system

　2 user

　3 repository

自上往下，范围越小，优先级越高。

系统级的配置文件路径在 --system

• Windows - C:\Program Files (x86)\Git\etc\gitconfig
• Linux - /etc/gitconfig
• Mac OS X - /usr/local/git/etc/gitconfig

用户级的配置文件路径在 --global

• Windows - C:\Users\<UserName>\.gitconfig
• Linux - ~/.gitconfig
• Mac OS X - ~/.gitconfig

repository级别的配置文件路径在 --local

• Windows - C:\<MyRepoFolder>\.git\config
• Linux - ~/<MyRepoFolder>/.git/config
• Mac OS X - ~/<MyRepoFolder>/.git/config

git config --list 查看所有的配置

git config --list --local /--global/--system 查看仓库/用户/系统的配置

git config <variable.name> <value> 可以用来修改git 的配置文件

设置命令别名 

$ git config --global alias.co checkout
$ git config --global alias.br branch
$ git config --global alias.ci commit
$ git config --global alias.st status

 

$ git unstage myfile.txt
$ git reset HEAD myfile.txt

$ git config --global alias.last 'log -1 HEAD'

$ git config --global alias.undo 'reset --soft HEAD~1'

移除alias

git config --global --unset alias.cm

两个特殊符号的使用

~ 与 ^



修改上次提交的信息 

git commit --ament -m “new commit message”

本质上是同样的东西 做了二次提交 会从新生成一个hash 所以不建议在push之后在做这样的事情

查看文件修改的记录

git blame xxxx.yyy

git show hashcode 查看修改的具体内容

git cherry-pick hasecode

GitFlow