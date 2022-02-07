# game

### 团队开发git配置说明

```diff
- 确保按照moodle上的视频配置本地环境，成功运行项目，并看到游戏界面后再进行以下配置:
```
1. git config --global user.name 加你的github名字

   git config --global user.email 加你的github密码
2. git config --global credential.helper store (保存用户吗和密码)
3. 克隆此项目到本地任意位置：git clone https://github.com/Xinjiann/game.git
4. 将克隆的项目中所有文件（共四个）复制到之前配置的项目工程文件夹下，重复文件选择替换。复制后将克隆的文件夹删除
5. 在项目工程文件夹下使用git status指令，检查是否显示如下：

```
On branch main
Your branch is up to date with 'origin/main'.

nothing to commit, working tree clean
```
4. 到此配置完成

### 后期开发阶段代码提交流程

1. 功能开发完成后，确保本地运行没有报错再进行如下步骤提交
2. git status检查变更文件是否和本次开发文件一致
3. git branch + 分支名，创建一个新的分支，分支名可以自定义，无规则要求，尽量和本次开发有关，例如：git branch unit_attack
4. git checkout + 分支名，切换到刚刚创建的分支，例如：git checkout unit_attack
5. git add . 将内容从工作目录添加到暂存(. 代表所有变更文件)
6. git commit -m "aaa" (将aaa替换成此次改动的说明，例如 git commit -m "add attack method")
7. git pull origin main, 此步骤将检查是否和远程仓库冲突，大部分情况无冲突，可以继续下一步。若报错则代表有冲突，需要解决冲突后回到第5步。
8. git push origin + 当前分支名 （例如 git push origin unit_attack）
9. 浏览器访问[远程仓库地址](https://github.com/Xinjiann/game)，将会看到黄色提示，点击Compare & pull request
10. 下拉页面再次检查代码
11. 点击create pull request
12. 页面跳转后将地址发给我，我进行大致的代码审查，没问题的话我来合并代码
13. 一切结束后，git checkout main 回到主分支，准备进行接下来的开发

#### tips:

1. 每次开发新功能前，确保本地代码没有未提交的改动的情况下，可以使用git pull来确保本地代码和远程最新代码保持一致（非必要）。
2. git branch指令可以检查当前在哪个分支上

### 测试

完成 “团队开发git配置说明” 后，在本地将app/testing.txt文件里加入自己的名字，按照 “后期开发阶段代码提交流程” 提交到远程仓库。

### 项目集成到idea

1. idea安装sbt插件，打开idea的Preference（mac下快捷键是command+，），选择Plugins，搜索sbt，安装scala，重启idea
2. 点击下图选项

<div align=center><img width="390" height="190" src="https://github.com/Xinjiann/Leetcode-notes/blob/main/images/%E6%88%AA%E5%B1%8F2022-02-03%2000.04.58.png"/></div>

3. 左上角加号选择sbt Task，取消勾选Use sbt shell, task框里输入run
4. 点击绿色运行按钮即可运行项目
