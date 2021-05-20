package com.rpc.registry.instance;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author slorui
 * data 2021/5/20
 */

public class RegistryInstance {
    private String instanceId;
    private String ip;
    private int port;
    private double weight = 1.0D;
    private boolean healthy = true;
    private boolean enabled = true;
    private boolean ephemeral = true;
    private String clusterName;
    private String serviceName;
    private Map<String, String> metadata = new HashMap();

    public RegistryInstance() {
    }

    public RegistryInstance(String ip,int port){
        this.ip = ip;
        this.port = port;
    }


    public String getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isHealthy() {
        return this.healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public String getClusterName() {
        return this.clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, String value) {
        if (this.metadata == null) {
            this.metadata = new HashMap(4);
        }

        this.metadata.put(key, value);
    }


    @Override
    public String toString() {
        return "Instance{instanceId='" + this.instanceId + '\'' + ", ip='" + this.ip + '\'' + ", port=" + this.port + ", weight=" + this.weight + ", healthy=" + this.healthy + ", enabled=" + this.enabled + ", ephemeral=" + this.ephemeral + ", clusterName='" + this.clusterName + '\'' + ", serviceName='" + this.serviceName + '\'' + ", metadata=" + this.metadata + '}';
    }

    public String toInetAddr() {
        return this.ip + ":" + this.port;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Instance)) {
            return false;
        } else {
            Instance host = (Instance)obj;
            return strEquals(host.toString(), this.toString());
        }
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    private static boolean strEquals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

}
