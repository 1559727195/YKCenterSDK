package com.gizwits.gizwifisdk.api;

import com.gizwits.gizwifisdk.enumration.GizMessageStatus;
import com.gizwits.gizwifisdk.enumration.GizMessageType;

public class GizMessage {

   private String id;
   private GizMessageType type;
   private GizMessageStatus status;
   private String createdAt;
   private String updatedAt;
   private String content;


   protected String infoMasking() {
      return "GizMessage [id=" + this.id + ", type=" + (this.type == null?"null":this.type.name()) + ", status=" + (this.status == null?"null":this.status.name()) + "]";
   }

   public String getId() {
      return this.id;
   }

   protected void setId(String id) {
      this.id = id;
   }

   public GizMessageType getType() {
      return this.type;
   }

   protected void setType(GizMessageType type) {
      this.type = type;
   }

   public GizMessageStatus getStatus() {
      return this.status;
   }

   protected void setStatus(GizMessageStatus status) {
      this.status = status;
   }

   public String getCreatedAt() {
      return this.createdAt;
   }

   protected void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
   }

   public String getUpdatedAt() {
      return this.updatedAt;
   }

   protected void setUpdatedAt(String updatedAt) {
      this.updatedAt = updatedAt;
   }

   public String getContent() {
      return this.content;
   }

   protected void setContent(String content) {
      this.content = content;
   }

   public String toString() {
      return "GizMessage [id=" + this.id + ", type=" + this.type + ", status=" + this.status + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", content=" + this.content + "]";
   }
}
