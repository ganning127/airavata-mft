# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
"""Client and server classes corresponding to protobuf-defined services."""
import grpc

from airavata_mft_sdk.scp import SCPCredential_pb2 as scp_dot_SCPCredential__pb2


class SCPSecretServiceStub(object):
    """Missing associated documentation comment in .proto file."""

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.getSCPSecret = channel.unary_unary(
                '/org.apache.airavata.mft.credential.service.scp.SCPSecretService/getSCPSecret',
                request_serializer=scp_dot_SCPCredential__pb2.SCPSecretGetRequest.SerializeToString,
                response_deserializer=scp_dot_SCPCredential__pb2.SCPSecret.FromString,
                )
        self.createSCPSecret = channel.unary_unary(
                '/org.apache.airavata.mft.credential.service.scp.SCPSecretService/createSCPSecret',
                request_serializer=scp_dot_SCPCredential__pb2.SCPSecretCreateRequest.SerializeToString,
                response_deserializer=scp_dot_SCPCredential__pb2.SCPSecret.FromString,
                )
        self.updateSCPSecret = channel.unary_unary(
                '/org.apache.airavata.mft.credential.service.scp.SCPSecretService/updateSCPSecret',
                request_serializer=scp_dot_SCPCredential__pb2.SCPSecretUpdateRequest.SerializeToString,
                response_deserializer=scp_dot_SCPCredential__pb2.SCPSecretUpdateResponse.FromString,
                )
        self.deleteSCPSecret = channel.unary_unary(
                '/org.apache.airavata.mft.credential.service.scp.SCPSecretService/deleteSCPSecret',
                request_serializer=scp_dot_SCPCredential__pb2.SCPSecretDeleteRequest.SerializeToString,
                response_deserializer=scp_dot_SCPCredential__pb2.SCPSecretDeleteResponse.FromString,
                )


class SCPSecretServiceServicer(object):
    """Missing associated documentation comment in .proto file."""

    def getSCPSecret(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def createSCPSecret(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def updateSCPSecret(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def deleteSCPSecret(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_SCPSecretServiceServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'getSCPSecret': grpc.unary_unary_rpc_method_handler(
                    servicer.getSCPSecret,
                    request_deserializer=scp_dot_SCPCredential__pb2.SCPSecretGetRequest.FromString,
                    response_serializer=scp_dot_SCPCredential__pb2.SCPSecret.SerializeToString,
            ),
            'createSCPSecret': grpc.unary_unary_rpc_method_handler(
                    servicer.createSCPSecret,
                    request_deserializer=scp_dot_SCPCredential__pb2.SCPSecretCreateRequest.FromString,
                    response_serializer=scp_dot_SCPCredential__pb2.SCPSecret.SerializeToString,
            ),
            'updateSCPSecret': grpc.unary_unary_rpc_method_handler(
                    servicer.updateSCPSecret,
                    request_deserializer=scp_dot_SCPCredential__pb2.SCPSecretUpdateRequest.FromString,
                    response_serializer=scp_dot_SCPCredential__pb2.SCPSecretUpdateResponse.SerializeToString,
            ),
            'deleteSCPSecret': grpc.unary_unary_rpc_method_handler(
                    servicer.deleteSCPSecret,
                    request_deserializer=scp_dot_SCPCredential__pb2.SCPSecretDeleteRequest.FromString,
                    response_serializer=scp_dot_SCPCredential__pb2.SCPSecretDeleteResponse.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'org.apache.airavata.mft.credential.service.scp.SCPSecretService', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class SCPSecretService(object):
    """Missing associated documentation comment in .proto file."""

    @staticmethod
    def getSCPSecret(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/org.apache.airavata.mft.credential.service.scp.SCPSecretService/getSCPSecret',
            scp_dot_SCPCredential__pb2.SCPSecretGetRequest.SerializeToString,
            scp_dot_SCPCredential__pb2.SCPSecret.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def createSCPSecret(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/org.apache.airavata.mft.credential.service.scp.SCPSecretService/createSCPSecret',
            scp_dot_SCPCredential__pb2.SCPSecretCreateRequest.SerializeToString,
            scp_dot_SCPCredential__pb2.SCPSecret.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def updateSCPSecret(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/org.apache.airavata.mft.credential.service.scp.SCPSecretService/updateSCPSecret',
            scp_dot_SCPCredential__pb2.SCPSecretUpdateRequest.SerializeToString,
            scp_dot_SCPCredential__pb2.SCPSecretUpdateResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def deleteSCPSecret(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/org.apache.airavata.mft.credential.service.scp.SCPSecretService/deleteSCPSecret',
            scp_dot_SCPCredential__pb2.SCPSecretDeleteRequest.SerializeToString,
            scp_dot_SCPCredential__pb2.SCPSecretDeleteResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)