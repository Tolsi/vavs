package ru.tolsi.aobp.blockchain.waves.binary

import java.net.InetSocketAddress

import com.esotericsoftware.kryo.Kryo
import com.twitter.chill.IKryoRegistrar
import ru.tolsi.aobp.blockchain.base.{Signature32, Signature64, Signed, Transaction}
import ru.tolsi.aobp.blockchain.waves.{WavesPublicKeyAccount$, WavesBlockChain, WavesTransaction}
import ru.tolsi.aobp.blockchain.waves.binary.serializers._

class WavesKryoRegistrar extends IKryoRegistrar {
  override def apply(k: Kryo): Unit = {
    k.setRegistrationRequired(true)
    k.setReferences(false)
    k.register(classOf[ApplicationVersionV1], new ApplicationVersionV1Serializer)
    k.getRegistration(classOf[String]).setSerializer(new ByteLengthUtf8StringSerializer)
    k.register(classOf[HandshakeV1], new HandshakeV1Serializer)
    k.register(classOf[InetSocketAddress], new InetSocketAddressSerializer)
    k.register(classOf[NetworkMessage], new NetworkMessageSerializer)
    k.register(classOf[ScoreMessageContentV1], new ScoreMessageContentV1Serializer)
    k.register(classOf[GetBlockMessageContentV1], new GetBlockMessageContentV1Serializer)
    k.register(classOf[GetPeersMessageContentV1], new GetPeersMessageContentV1Serializer)
    k.register(classOf[PeersMessageContentV1], new PeersMessageContentV1Serializer)
    k.register(classOf[GetSignaturesMessageContentV1], new GetSignaturesMessageContentV1Serializer)
    k.register(classOf[SignaturesMessageContentV1], new SignaturesMessageContentV1Serializer)
    k.register(classOf[BlockContentV1], new BlockContentV1Serializer)
    k.register(classOf[BlockContentV2], new BlockContentV2Serializer)
    k.register(classOf[SignedBlock[_ <: BlockContent]], new SignedBlockSerializer)
    k.register(classOf[GenesisTransactionV1], new GenesisTransactionV1Serializer)
    k.register(classOf[PaymentTransactionV1], new PaymentTransactionV1Serializer)
    k.register(classOf[PublicKeyAccount], new PublicKeyAccountSerializer)
    k.register(classOf[WavesPublicKeyAccount], new AccountSerializer)
    k.register(classOf[Signed[WavesTransaction, Signature64]], new SignedTransactionSerializer)
    k.register(classOf[Signature64], new Signature64Serializer)
    k.register(classOf[Signature32], new Signature32Serializer)
    k.register(classOf[StateChange], new StateChangeSerializer)
    k.register(classOf[StateRecord], new StateRecordSerializer)
    k.register(classOf[Credentials], new CredentialsSerializer)
    k.register(classOf[ConsensusDataV1], new ConsensusDataV1Serializer)
    k.register(classOf[TransactionsDataV1], new TransactionsDataV1Serializer)
    k.register(classOf[BlockCheckpoint], new BlockCheckpointSerializer)
    k.register(classOf[BlockCheckpointSet], new BlockCheckpointSetSerializer)
    k.register(classOf[CheckpointMessageContentV1], new CheckpointMessageContentV1Serializer)
  }
}

