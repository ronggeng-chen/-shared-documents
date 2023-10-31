#### GitHub SSH CN

##### 0.准备


```shell
## 检查本地否有ssh key,如果存在则跳过第一步
cd ~/.ssh


```

##### 1.生成SSH私钥

```shell
## 生成私钥命令
$ ssh-keygen -t ed25519 -C "XXXXX@gmail.com"

> Generating public/private ed25519 key pair.
## 可选:可输入名称;默认值 id_ed25519 (根据秘钥类型)
> Enter file in which to save the key (C:\Users\17578/.ssh/id_ed25519):

## 输入密码
> Enter passphrase (empty for no passphrase):

## 再次输入密码
> Enter same passphrase again:


## 参数含义：
## -t： 指定密钥类型，默认rsa，可省略
##      RSA（默认）：RSA 密钥对，通常使用 -t rsa 指定。
##      DSA：DSA 密钥对，使用 -t dsa 指定。
##      ECDSA：ECDSA（椭圆曲线数字签名算法）密钥对，使用 -t ecdsa 指定。
##      Ed25519：Ed25519 密钥对，使用 -t ed25519 指定。这是一种现代、高度安全的公钥加密算法。
## -C： 设置注释文字，比如邮箱（会放在公钥里）
## -f： 指定密钥文件存储文件名 

```

##### 2.查看私钥

```shell
cd ~/.ssh

## 编辑工具打开 *.pub 文件
## 例如编辑工具打开 id_ed25519.pub
## 复制全部内容
```

##### 3.github配置密钥
 
* 登录github.com 点击你的头像，然后选择"Settings"
* 点击"SSH and GPG keys"（SSH 和 GPG 密钥）
* 点击右上角的"New SSH key"（新建SSH密钥）,在打开的页面中，标题可以随意，但在"Key"的文本框中，粘贴你刚刚复制的公钥内容
* 点击"Add SSH key"（添加SSH密钥）来保存

##### 4.idea中使用

###### 4.1.已clone项目
* 打开你的IntelliJ IDEA,点击右上角的"File" > "Settings"
* 选择"Tools" > "SSH Configurations"
```shell
## "Name"，可以填写你喜欢的任何名称。
## "Host"，如果你是连接到GitHub，那么就填写github.com。
## "Port"，保持默认的22即可。
## "User name"，如果你是连接到GitHub，那么就填写git。
## "Authentication type"，选择"Key pair (OpenSSH or PuTTY)"。
## "Private key file"，点击"..."按钮，然后浏览到你的私钥文件（在Git Bash生成的密钥文件）。
```
* 选择顶部菜单"Git" > 远程管理 更改origin中url地址为github ssh项目地址


