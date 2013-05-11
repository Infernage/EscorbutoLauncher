/*
 * BlockInfo
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package elr.xz_coder.index;

import elr.xz_coder.common.StreamFlags;

public class BlockInfo {
    public StreamFlags streamFlags;
    public long compressedOffset;
    public long uncompressedOffset;
    public long unpaddedSize;
    public long uncompressedSize;
}
