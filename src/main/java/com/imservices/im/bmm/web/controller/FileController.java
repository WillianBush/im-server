package com.imservices.im.bmm.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.AccessRecord;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.service.AccessRecordService;
import com.imservices.im.bmm.service.FileSystemService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.RoomService;
import com.imservices.im.bmm.utils.MD5;
import com.imservices.im.bmm.utils.oss.UploadResp;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Controller("FileController")
@RequestMapping(value = "/user/file")
@CrossOrigin
@Slf4j
@AllArgsConstructor
public class FileController extends BaseController {

    private MemberService memberService;
    private AccessRecordService accessRecordService;
    private RoomService roomService;
    private FileSystemService fileSystemService;
    private ChatStoreComponent chatStoreComponent;
    private RedisService redisService;

    @RequestMapping(value = {"/getDomain"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void getDomain(HttpServletRequest request, HttpServletResponse response) {
        try {
            ResponseUtils.json(response, 200, fileSystemService.getDomain(), null);
        } catch (Exception e) {
            log.error("", e);
        }
    }


    @RequestMapping(value = {"/delB64Img"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @AuthPassport
    public void delB64Img(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = request.getParameter("path");
            if (path.contains("/")) {
                String fileName = path.split("/")[path.split("/").length - 1];
                fileSystemService.deleteFile("chat_img", "img_sys/upload/chat/uploadB64Img/" + fileName);
            } else {
                fileSystemService.deleteFile("chat_img", "img_sys/upload/chat/uploadB64Img/");
            }
//			//
            //FileProcess.del(request.getRealPath(path));
            // TO DO: oss 路径: img_sys/upload/chat/uploadB64Img/ @shenghong
//            FTPUtil.deleteFile("chat_img", path.substring(path.lastIndexOf("/") + 1, path.length()));
            fileSystemService.deleteFile("chat_img", "img_sys/upload/chat/uploadB64Img/");

            ResponseUtils.json(response, 200, "success", null);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @RequestMapping(value = {"/uploadB64Img"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @AuthPassport
    public void uploadB64Img(HttpServletRequest request, HttpServletResponse response) {
//        log.info("uploadB64Img ---------- ");

        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        String base64With3 = request.getParameter("base64");
        String md5 = MD5.MD5EncodeUTF8(base64With3);
        String path = redisService.easyLockIfExistReturnWait("uploadB64Img:"+MEMBERID+":"+md5,15L,()->{
            try {
                String ossPath = "img_sys/upload/chat/uploadB64Img";
                String fileStuff = ".png";
//                String base64 = URLDecoder.decode(base64Encode, StandardCharsets.UTF_8);
                String base64 = base64With3.replaceAll("#","+");
//                log.info("base64,length:{}, data :{}",base64.length(),base64);
                if (base64.contains("data:image/png;base64,")) {
                    base64 = base64.replaceAll("data:image/png;base64,", "");
                }
                if (base64.contains("data:image/jpeg;base64,")) {
                    base64 = base64.replaceAll("data:image/jpeg;base64,", "");
                    fileStuff = ".jpg";
                }
                String fn = UUID.randomUUID().toString().replaceAll("-", "");
                byte[] imgBytes = null;
                try {
                    imgBytes = Base64.getDecoder().decode(base64.getBytes());

                    for (int i = 0; i < imgBytes.length; ++i) {
                        if (imgBytes[i] < 0) {// 调整异常数据
                            imgBytes[i] += 256;
                        }
                    }

//                    imgBytes = Base64Utils.decodeFromString(base64);
                }catch (Exception e){
                    log.error("base64解密失败, base64:{}",base64);
                    return "";
                }
                if (imgBytes.length >0){
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imgBytes);
                    UploadResp resp = fileSystemService.uploadObject(inputStream, "image" + fn+ fileStuff, ossPath, (long) imgBytes.length);
                    return removeDomain(resp.getFilePath());
                }
            } catch (Exception e) {
                log.error("上传文件失败", e);
            }
                return "";
        });
        if (StringUtils.isEmpty(path)){
            ResponseUtils.json(response, 500, "上传过于频繁", null);
            return;
        }
        ResponseUtils.json(response, 200, path, null);
    }


    @RequestMapping(value = {"/uploadVideo"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @AuthPassport
    public void uploadVideo(@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String ossPath = "img_sys/upload/chat/video";
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        MemberBean mb = chatStoreComponent.getMemberBean(MEMBERID);

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String now = format.format(System.currentTimeMillis());
            Random r = new Random();
            int sleep = r.nextInt(2000) + 500;
            String vFileName = mb.getMemberId() + "-" + now + "-" + sleep +  fileExtension;
            UploadResp resp = fileSystemService.uploadObject(file.getInputStream(), vFileName, ossPath, file.getSize());
            ResponseUtils.json(response, 200, removeDomain(resp.getFilePath()), null);
        } catch (Exception e) {
            log.error("上传视频失败", e);
        }
    }


    @RequestMapping(value = {"/uploadVoice"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @AuthPassport
    public void uploadVoice(@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        String ossPath = "img_sys/upload/chat/voice";
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));

        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        log.error("error::::uploadVoice::::::::::{}",MEMBERID);
        if (MEMBERID == null) {
            throw new RuntimeException("未登录");
        }
        MemberBean mb = chatStoreComponent.getMemberBean(MEMBERID);

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String now = format.format(System.currentTimeMillis());
            Random r = new Random();
            int sleep = r.nextInt(2000) + 500;
            String voiceFileName = mb.getMemberId() + "-" + now + "-" + sleep + fileExtension;
            UploadResp resp = fileSystemService.uploadObject(file.getInputStream(), voiceFileName, ossPath, file.getSize());
            ResponseUtils.json(response, 200, removeDomain(resp.getFilePath()), null);
        } catch (Exception e) {
            log.error("上传视频失败", e);
        }
    }

//	public static void main(String[] args) {
//		String str = "data:audio/x-wav;base64,IyFBTVIKPJEXFr5meeHgAeev8AAAAIAAAAAAAAA";
//		String[] ss = GroupMethod.group("data:audio/(.*?);base64,", str, 1);
//		String temp = "data:audio/"+ss[0]+";base64,";
//		String new_str = str.replaceAll(temp, "");
//		//
//	}

    public static String decryptByBase64(String base64, String filePath) {
        if (base64 == null && filePath == null) {
            return "生成文件失败，请给出相应的数据。";
        }
        try {
            log.info("base64:{}",base64);
            Files.write(Paths.get(filePath), Base64.getDecoder().decode(base64), StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error("", e);
        }
        return "指定路径下生成文件成功！";
    }

    @RequestMapping(value = {"/upload"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @AuthPassport
    public void upload(@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        String fileName = file.getOriginalFilename();  // 文件名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String ossPath = "img_sys/upload/chat";

        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            try {
                Random r = new Random();
                int sleep = r.nextInt(2000) + 500;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String now = format.format(System.currentTimeMillis());
                String imageName = "image" + now + "-" + sleep + fileExtension;
                UploadResp resp = fileSystemService.uploadObject(file.getInputStream(), imageName, ossPath, file.getSize());
                String uri = removeDomain(resp.getFilePath());
                ResponseUtils.json(response, 200, fileSystemService.removeBucketName(uri), null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        } else {
            try {
                ResponseUtils.json(response, 500, "文件格式错误!", null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        }
    }

    @RequestMapping(value = {"/uploads"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @AuthPassport
    public void uploads(@RequestParam(value = "file", required = true) List<MultipartFile> files, HttpServletRequest request, HttpServletResponse response) {

        if (files.isEmpty()) {
            ResponseUtils.json(response, 500, "参数错误!", null);
        }
        List<String> respPath = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();  // 文件名
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String ossPath = "img_sys/upload/chat";
            Random r = new Random();
            int sleep = r.nextInt(2000) + 500;

            if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    String now = format.format(System.currentTimeMillis());
                    String imageName = "image" + now + "-pl" + sleep + fileExtension;
                    UploadResp resp = fileSystemService.uploadObject(file.getInputStream(), imageName, ossPath, file.getSize());

                    respPath.add(removeDomain(resp.getFilePath()));
//                    ResponseUtils.json(response, 200, removeDomain(resp.getFilePath()), null);
                } catch (Exception e) {
                    log.error("上传失败", e);
                }
            } else {
                try {
                    respPath.add("文件格式错误");
//                    ResponseUtils.json(response, 500, "文件格式错误!", null);
                } catch (Exception e) {
                    log.error("上传失败", e);
                }
            }
        }
        ResponseUtils.json(response, 500, respPath, null);
    }

    @RequestMapping(value = {"/uploadInvitePic"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @AuthPassport
    public void uploadInvitePic(@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        String fileName = file.getOriginalFilename();  // 文件名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String ossPath = "img_sys/upload/invite";

        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String now = format.format(System.currentTimeMillis());
                String imageName = "invite" + now + fileExtension;
                UploadResp resp = fileSystemService.uploadObject(file.getInputStream(), imageName, ossPath, file.getSize());

                ResponseUtils.json(response, 200, removeDomain(resp.getFilePath()), null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        } else {
            try {
                ResponseUtils.json(response, 500, "文件格式错误!", null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        }
    }


    @AuthPassport
    @RequestMapping(value = {"/uploadHeadpic"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void uploadHeadpic(@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        String fileName = file.getOriginalFilename();  // 文件名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String ossPath = "img_sys/upload/member";

        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String now = format.format(System.currentTimeMillis());
                String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
                MemberBean mb = chatStoreComponent.getMemberBean(MEMBERID);
                String iconName = mb.getMemberId() + "-" + now + fileExtension;

                UploadResp resp = fileSystemService.uploadObject(file.getInputStream(), iconName, ossPath, file.getSize());
                String imageOssPath = removeDomain(resp.getFilePath());
                try {
                    fileSystemService.deleteFile(mb.getHeadpic());
                } catch (Exception e) {
                    log.error("删除图片失败", e.getCause());
                }
                mb.setHeadpic(fileSystemService.removeBucketName(imageOssPath));
                //此处保存头像失败
//                memberService.update(new String[]{"headpic"}, new Object[]{mb.getHeadpic()}, "where id='" + mb.getId() + "'");
                Member member = memberService.get( mb.getId());
//                log.info("+++++++++++++++++++++++++++mb.getHeadpic(imageOssPath):3:" + member.getHeadpic());
                member.setHeadpic(fileSystemService.removeBucketName(imageOssPath));
                memberService.update(member);
                accessRecordService.updateHeadpic(mb.getId(), mb.getHeadpic());
                ResponseUtils.json(response, 200, imageOssPath, null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        } else {
            try {
                ResponseUtils.json(response, 500, "文件格式错误!", null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        }
    }


    @AuthPassport
    @RequestMapping(value = {"/uploadRoomHeadpic"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void uploadRoomHeadpic(@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        String fileName = file.getOriginalFilename();  // 文件名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String ossPath = "img_sys/upload/room";
        String roomid = request.getHeader("x-access-roomid");
//        RoomBean roomBeanCache = ChatStore.ROOMB_BEAN_MAP.get(roomid);
        RoomBean roomBeanCache = chatStoreComponent.getRoomBeanMap(roomid);
        if (null == roomBeanCache){
            try {
                ResponseUtils.json(response, 500, "群组不存在!", null);
            } catch (Exception e) {
                log.error("",e);
            }
            return;
        }
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String now = format.format(System.currentTimeMillis());
                Member member = super.getUserInfo(request);
                log.error("member:{}", JSONObject.toJSONString(member));
                log.error("roomBeanCache:{}", JSONObject.toJSONString(roomBeanCache));
                if (!member.getId().equals(roomBeanCache.getOwner_UUID()) && !roomBeanCache.getMemberMgr_ids().contains(member.getId())) {
                    ResponseUtils.json(response, 500, "无权限!", null);
                    return;
                }

                String iconName = roomid + "-" + now + fileExtension;
                UploadResp resp = fileSystemService.uploadObject(file.getInputStream(), iconName, ossPath, file.getSize());

                try {
                    fileSystemService.deleteFile(roomBeanCache.getImg());
                } catch (Exception e) {
                    log.error("删除图片失败", e);
                }

                roomBeanCache.setImg(removeDomain(resp.getFilePath()));
                roomBeanCache.setUseCustomHeadpic(1);
                roomService.update(BeanUtils.roomBeanTransferToRoomSimple(roomBeanCache));
                List<AccessRecord> accessRecords= accessRecordService.getList(new String[]{"entityid"},new String[]{roomid});
                CompletableFuture.runAsync(()->{
                    for (AccessRecord accessRecord : accessRecords) {
                        accessRecord.setImg(roomBeanCache.getImg());
                        try {
                            accessRecordService.update(accessRecord);
                        } catch (Exception e) {
                            log.error("",e);
                        }
                    }
                });
                ResponseUtils.json(response, 200, removeDomain(resp.getFilePath()), null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        } else {
            ResponseUtils.json(response, 500, "文件格式错误!", null);
        }
    }


    @AuthPassport
    @RequestMapping(value = {"/chat/uploadPic"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void chatUploadPic(@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        // TODO: @shenghong
        String fileName = file.getOriginalFilename();  // 文件名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String ossPath = "img_sys/upload/room";

        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String now = format.format(System.currentTimeMillis());
                Member member = super.getUserInfo(request);

                String iconName = "chat" +"-" + now + fileExtension;
                UploadResp resp = fileSystemService.uploadObject(file.getInputStream(), iconName, ossPath, file.getSize());

                ResponseUtils.json(response, 200, removeDomain(resp.getFilePath()), null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        } else {
            try {
                ResponseUtils.json(response, 500, "文件格式错误!", null);
            } catch (Exception e) {
                log.error("上传失败", e);
            }
        }
    }


    public static String removeDomain(String path) {
        if (path.contains("http")) {
            try {
                java.net.URL url = new java.net.URL(path);
                return url.getPath();
            } catch (MalformedURLException e) {
                log.error("获取路径失败");
            }
        }
        return null;
    }


    public static void main(String[] args) {
        String base64 = "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";
        Base64.getDecoder().decode(base64);
    }
}
