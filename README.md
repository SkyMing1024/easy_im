# easy_im

## 启动报错:chat-client.jar中没有主清单属性
普通maven项目
java -jar 启动报错
chat-client.jar中没有主清单属性
没有指定启动类
解决：
```aidl
        <plugin>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.0.2</version>
          <configuration>
            <archive>
              <manifest>
                <mainClass>com.sky.easyIM.ServerApplication</mainClass>
              </manifest>
            </archive>
          </configuration>
        </plugin>

maven-jar-plugin插件中增加配置
```


## server、client模块分别启动时，居然会加载对方的方法