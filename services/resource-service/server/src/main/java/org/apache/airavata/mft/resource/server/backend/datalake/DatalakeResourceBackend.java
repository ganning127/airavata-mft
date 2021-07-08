/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.airavata.mft.resource.server.backend.datalake;

import com.google.protobuf.Struct;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.airavata.datalake.drms.DRMSServiceAuthToken;
import org.apache.airavata.datalake.drms.storage.ResourceFetchRequest;
import org.apache.airavata.datalake.drms.storage.ResourceFetchResponse;
import org.apache.airavata.datalake.drms.storage.ResourceServiceGrpc;
import org.apache.airavata.datalake.drms.storage.preference.s3.S3StoragePreference;
import org.apache.airavata.datalake.drms.storage.preference.ssh.SSHStoragePreference;
import org.apache.airavata.datalake.drms.storage.ssh.SSHStorage;
import org.apache.airavata.mft.common.AuthToken;
import org.apache.airavata.mft.common.PasswordAuth;
import org.apache.airavata.mft.resource.server.backend.ResourceBackend;
import org.apache.airavata.mft.resource.stubs.azure.storage.*;
import org.apache.airavata.mft.resource.stubs.box.storage.*;
import org.apache.airavata.mft.resource.stubs.common.*;
import org.apache.airavata.mft.resource.stubs.dropbox.storage.*;
import org.apache.airavata.mft.resource.stubs.ftp.storage.*;
import org.apache.airavata.mft.resource.stubs.gcs.storage.*;
import org.apache.airavata.mft.resource.stubs.local.storage.*;
import org.apache.airavata.mft.resource.stubs.s3.storage.*;
import org.apache.airavata.mft.resource.stubs.scp.storage.*;
import org.apache.custos.clients.CustosClientProvider;
import org.apache.custos.identity.management.client.IdentityManagementClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DatalakeResourceBackend implements ResourceBackend {

    private static final Logger logger = LoggerFactory.getLogger(DatalakeResourceBackend.class);

    private ManagedChannel channel;

    @org.springframework.beans.factory.annotation.Value("${datalake.backend.custos.client.id}")
    private String clientId;

    @org.springframework.beans.factory.annotation.Value("${datalake.backend.custos.client.secret}")
    private String clientSecret;

    @Override
    public void init() {
        channel = ManagedChannelBuilder.forAddress("localhost", 7070).usePlaintext().build();
    }

    @Override
    public void destroy() {
        try {
            channel.shutdown();
        } catch (Exception e) {
            logger.error("Failed to gracefully terminate DRMS API channel");
        }
    }

    @Override
    public Optional<GenericResource> getGenericResource(GenericResourceGetRequest request) throws Exception {

        AuthToken authzToken = request.getAuthzToken();
        PasswordAuth passwordAuth = authzToken.getPasswordAuth();
        String accessToken = getAccessToken(clientId, clientSecret, passwordAuth.getUserName(), passwordAuth.getPassword());

        if (accessToken == null) {
            logger.error("Failed to fetch an access token when fetching a resource with id {} to user {}",
                    request.getResourceId(), passwordAuth.getUserName());
            throw new Exception("\"Failed to fetch an access token when fetching a resource with id " + request.getResourceId() +
                    " to user " + passwordAuth.getUserName());
        }

        ResourceServiceGrpc.ResourceServiceBlockingStub datalakeResourceStub = ResourceServiceGrpc.newBlockingStub(channel);
        ResourceFetchResponse resourceFetchResponse = datalakeResourceStub.fetchResource(ResourceFetchRequest.newBuilder()
                .setAuthToken(DRMSServiceAuthToken.newBuilder().setAccessToken(accessToken).build())
                .setResourceId(request.getResourceId())
                .build());

        if (resourceFetchResponse.getResource().getResourceId().isEmpty()) {
            return Optional.empty();
        }

        org.apache.airavata.datalake.drms.resource.GenericResource resource = resourceFetchResponse.getResource();
        GenericResource.Builder resourceBuilder = GenericResource.newBuilder()
                .setResourceId(resource.getResourceId());

        switch (resource.getType()) {
            case "FILE":
                resourceBuilder.setFile(FileResource.newBuilder()
                        .setResourcePath(resource.getResourcePath()).build());
                break;
            case "COLLECTION":
                resourceBuilder.setDirectory(DirectoryResource.newBuilder()
                        .setResourcePath(resource.getResourcePath()).build());
                break;
        }

        switch (resource.getStoragePreferenceCase()) {
            case SSH_PREFERENCE:
                SSHStoragePreference sshPreference = resource.getSshPreference();
                SSHStorage storage = sshPreference.getStorage();
                resourceBuilder.setScpStorage(SCPStorage.newBuilder()
                        .setStorageId(storage.getStorageId()).setHost(storage.getHostName())
                        .setPort(storage.getPort())
                        .setUser(sshPreference.getUserName()).build());
                break;
            case S3_PREFERENCE:
                S3StoragePreference s3Preference = resource.getS3Preference();
                org.apache.airavata.datalake.drms.storage.s3.S3Storage s3Storage = s3Preference.getStorage();
                resourceBuilder.setS3Storage(S3Storage.newBuilder()
                        .setStorageId(s3Storage.getStorageId())
                        .setBucketName(s3Storage.getBucketName())
                        .setRegion(s3Storage.getRegion()).build());
                break;
            case STORAGEPREFERENCE_NOT_SET:
                logger.error("No preference registered for the resource {}", request.getResourceId());
                throw new Exception("No preference registered for the resource " + request.getResourceId());
        }

        return Optional.of(resourceBuilder.build());
    }

    @Override
    public GenericResource createGenericResource(GenericResourceCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateGenericResource(GenericResourceUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteGenericResource(GenericResourceDeleteRequest request) throws Exception {
        return false;
    }

    @Override
    public Optional<SCPStorage> getSCPStorage(SCPStorageGetRequest request) throws Exception {
        return Optional.empty();
    }

    @Override
    public SCPStorage createSCPStorage(SCPStorageCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateSCPStorage(SCPStorageUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteSCPStorage(SCPStorageDeleteRequest request) throws Exception {
        return false;
    }

    @Override
    public Optional<LocalStorage> getLocalStorage(LocalStorageGetRequest request) throws Exception {
        return Optional.empty();
    }

    @Override
    public LocalStorage createLocalStorage(LocalStorageCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateLocalStorage(LocalStorageUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteLocalStorage(LocalStorageDeleteRequest request) throws Exception {
        return false;
    }

    @Override
    public Optional<S3Storage> getS3Storage(S3StorageGetRequest request) throws Exception {
        return Optional.empty();
    }

    @Override
    public S3Storage createS3Storage(S3StorageCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateS3Storage(S3StorageUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteS3Storage(S3StorageDeleteRequest request) throws Exception {
        return false;
    }

    @Override
    public Optional<BoxStorage> getBoxStorage(BoxStorageGetRequest request) throws Exception {
        return Optional.empty();
    }

    @Override
    public BoxStorage createBoxStorage(BoxStorageCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateBoxStorage(BoxStorageUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteBoxStorage(BoxStorageDeleteRequest request) throws Exception {
        return false;
    }

    @Override
    public Optional<AzureStorage> getAzureStorage(AzureStorageGetRequest request) throws Exception {
        return Optional.empty();
    }

    @Override
    public AzureStorage createAzureStorage(AzureStorageCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateAzureStorage(AzureStorageUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteAzureStorage(AzureStorageDeleteRequest request) throws Exception {
        return false;
    }

    @Override
    public Optional<GCSStorage> getGCSStorage(GCSStorageGetRequest request) throws Exception {
        return Optional.empty();
    }

    @Override
    public GCSStorage createGCSStorage(GCSStorageCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateGCSStorage(GCSStorageUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteGCSStorage(GCSStorageDeleteRequest request) throws Exception {
        return false;
    }

    @Override
    public Optional<DropboxStorage> getDropboxStorage(DropboxStorageGetRequest request) throws Exception {
        return Optional.empty();
    }

    @Override
    public DropboxStorage createDropboxStorage(DropboxStorageCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateDropboxStorage(DropboxStorageUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteDropboxStorage(DropboxStorageDeleteRequest request) throws Exception {
        return false;
    }

    @Override
    public Optional<FTPStorage> getFTPStorage(FTPStorageGetRequest request) throws Exception {
        return Optional.empty();
    }

    @Override
    public FTPStorage createFTPStorage(FTPStorageCreateRequest request) throws Exception {
        return null;
    }

    @Override
    public boolean updateFTPStorage(FTPStorageUpdateRequest request) throws Exception {
        return false;
    }

    @Override
    public boolean deleteFTPStorage(FTPStorageDeleteRequest request) throws Exception {
        return false;
    }


    private String getAccessToken(String clientId, String clientSecret, String userName, String password) {
        try {

            CustosClientProvider custosClientProvider = new CustosClientProvider.Builder().setServerHost("custos.scigap.org")
                    .setServerPort(31499)
                    .setClientId(clientId)
                    .setClientSec(clientSecret).build();

            IdentityManagementClient identityManagementClient = custosClientProvider.getIdentityManagementClient();
            Struct struct = identityManagementClient.getToken(null, null, userName, password, null, "password");
            return struct.getFieldsMap().get("access_token").getStringValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}