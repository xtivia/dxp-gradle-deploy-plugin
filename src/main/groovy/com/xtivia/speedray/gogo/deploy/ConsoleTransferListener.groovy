/**
 * Copyright (c) 2016 Xtivia, Inc. All rights reserved.
 *
 * This file is part of the Xtivia Services Framework (XSF) library.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.speedray.gogo.deploy

import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.transfer.AbstractTransferListener
import org.eclipse.aether.transfer.MetadataNotFoundException
import org.eclipse.aether.transfer.TransferEvent
import org.eclipse.aether.transfer.TransferResource

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.concurrent.ConcurrentHashMap

class ConsoleTransferListener
        extends AbstractTransferListener {

    private PrintStream out

    private Map<TransferResource, Long> downloads = new ConcurrentHashMap<TransferResource, Long>()
    public Map<String, TransferResource> downloadTransfers = new ConcurrentHashMap<>()

    private int lastLength

    ConsoleTransferListener() {
        this(null)
    }

    ConsoleTransferListener(PrintStream out) {
        this.out = (out != null) ? out : System.out
    }

    @Override
    void transferInitiated(TransferEvent event) {
        String message = event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploading" : "Downloading"

        out.println(message + ": " + event.getResource().getRepositoryUrl() + event.getResource().getResourceName())
    }

    @Override
    void transferProgressed(TransferEvent event) {
        TransferResource resource = event.getResource()
        downloads.put(resource, Long.valueOf(event.getTransferredBytes()))

        StringBuilder buffer = new StringBuilder(64)

        for (Map.Entry<TransferResource, Long> entry : downloads.entrySet()) {
            long total = entry.getKey().getContentLength()
            long complete = entry.getValue().longValue()

            buffer.append(getStatus(complete, total)).append("  ")
        }

        int pad = lastLength - buffer.length()
        lastLength = buffer.length()
        this.pad(buffer, pad)
        buffer.append('\r')

        out.print(buffer)
    }

    private String getStatus(long complete, long total) {
        if (total >= 1024) {
            return toKB(complete) + "/" + toKB(total) + " KB "
        } else if (total >= 0) {
            return complete + "/" + total + " B "
        } else if (complete >= 1024) {
            return toKB(complete) + " KB "
        } else {
            return complete + " B "
        }
    }

    private void pad(StringBuilder buffer, int spaces) {
        String block = "                                        "
        while (spaces > 0) {
            int n = Math.min(spaces, block.length())
            buffer.append(block, 0, n)
            spaces -= n
        }
    }

    @Override
    void transferSucceeded(TransferEvent event) {
        transferCompleted(event)

        TransferResource resource = event.getResource()
        if(resource.resourceName.endsWith('jar') || resource.resourceName.endsWith('war')) {
            downloadTransfers.put(resource.trace.parent.data, resource)
        }
        long contentLength = event.getTransferredBytes()
        if (contentLength >= 0) {
            String type = (event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploaded" : "Downloaded")
            String len = contentLength >= 1024 ? toKB(contentLength) + " KB" : contentLength + " B"

            String throughput = ""
            long duration = System.currentTimeMillis() - resource.getTransferStartTime()
            if (duration > 0) {
                long bytes = contentLength - resource.getResumeOffset()
                DecimalFormat format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH))
                double kbPerSec = (bytes / 1024.0) / (duration / 1000.0)
                throughput = " at " + format.format(kbPerSec) + " KB/sec"
            }

            out.println(type + ": " + resource.getRepositoryUrl() + resource.getResourceName() + " (" + len
                    + throughput + ")")
        }
    }

    @Override
    void transferFailed(TransferEvent event) {
        transferCompleted(event)

        if (!(event.getException() instanceof MetadataNotFoundException)) {
            event.getException().printStackTrace(out)
        }
    }

    private void transferCompleted(TransferEvent event) {
        downloads.remove(event.getResource())

        StringBuilder buffer = new StringBuilder(64)
        pad(buffer, lastLength)
        buffer.append('\r')
        out.print(buffer)
    }

    void transferCorrupted(TransferEvent event) {
        event.getException().printStackTrace(out)
    }

    protected long toKB(long bytes) {
        return (bytes + 1023) / 1024
    }
}